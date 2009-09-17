package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.derivedFrom;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.ONBOARD_CARRIER;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.CLAIM;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.UNLOAD;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Date;

/**
 * Everything about the delivery of the cargo, i.e. where the cargo is
 * right now, whether or not it's routed, misdirected and so on.
 */
public class Delivery implements ValueObject<Delivery> {

  private HandlingActivity mostRecentHandlingActivity;
  private Date calculatedAt;

  // TODO these will be replaced by domain events
  private boolean misdirected;
  private boolean isUnloadedAtDestination;
  private RoutingStatus routingStatus;

  /**
   * Derives a new delivery snapshot to reflect changes in routing, i.e.
   * when the route specification or the itinerary has changed
   * but no additional handling of the cargo has been performed.
   *
   * @param routeSpecification route specification
   * @param itinerary          itinerary
   * @return An up to date delivery
   */
  Delivery withRoutingChange(final RouteSpecification routeSpecification, final Itinerary itinerary) {
    Validate.notNull(routeSpecification, "Route specification is required");

    final RoutingStatus newRoutingStatus = derivedFrom(itinerary, routeSpecification);
    boolean misdirected = false;

    return new Delivery(mostRecentHandlingActivity, misdirected, isUnloadedAtDestination, newRoutingStatus);
  }

  /**
   * Derives a new delivery snapshot to reflect that the cargo has been handled.
   *
   * @param routeSpecification  route specification
   * @param itinerary           itinerary
   * @param handlingActivity    handling activity
   * @return An up to date delivery
   */
  Delivery whenHandled(final RouteSpecification routeSpecification, final Itinerary itinerary, final HandlingActivity handlingActivity) {
    Validate.notNull(routeSpecification, "Route specification is required");
    Validate.notNull(itinerary, "Itinerary is required");

    final boolean newMisdirected = misdirectionStatus(itinerary, handlingActivity);
    final boolean newUnloadedAtDestination = unloadedAtDestination(routeSpecification, handlingActivity);
    final RoutingStatus newRoutingStatus = this.routingStatus;

    return new Delivery(handlingActivity, newMisdirected, newUnloadedAtDestination, newRoutingStatus);
  }

  /**
   * @param routeSpecification route specification
   * @param itinerary itinerary
   * @return Initial delivery, before any handling has taken place
   */
  static Delivery initial(final RouteSpecification routeSpecification, final Itinerary itinerary) {
    Validate.notNull(routeSpecification, "Route specification is required");

    final boolean newMisdirected = misdirectionStatus(itinerary, null);
    final boolean newUnloadedAtDestination = unloadedAtDestination(routeSpecification, null);
    final RoutingStatus newRoutingStatus = derivedFrom(itinerary, routeSpecification);

    return new Delivery(null, newMisdirected, newUnloadedAtDestination, newRoutingStatus);
  }

  private Delivery(final HandlingActivity mostRecentHandlingActivity,
                   final boolean misdirected,
                   final boolean unloadedAtDestination,
                   final RoutingStatus routingStatus) {
    this.mostRecentHandlingActivity = mostRecentHandlingActivity;
    this.misdirected = misdirected;
    this.isUnloadedAtDestination = unloadedAtDestination;
    this.routingStatus = routingStatus;
    this.calculatedAt = new Date();
  }

  /**
   * @return Transport status
   */
  public TransportStatus transportStatus() {
    return TransportStatus.derivedFrom(mostRecentHandlingActivity);
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    if (mostRecentHandlingActivity != null) {
      return mostRecentHandlingActivity.location();
    } else {
      return Location.UNKNOWN;
    }
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
    if (mostRecentHandlingActivity != null && transportStatus().equals(ONBOARD_CARRIER)) {
      return mostRecentHandlingActivity.voyage();
    } else {
      return Voyage.NONE;
    }
  }

  /**
   * Check if cargo is misdirected.
   * <p/>
   * <ul>
   * <li>A cargo is misdirected if it is in a location that's not in the itinerary.
   * <li>A cargo with no itinerary can not be misdirected.
   * <li>A cargo that has received no handling events can not be misdirected.
   * </ul>
   *
   * @return <code>true</code> if the cargo has been misdirected,
   */
  public boolean isMisdirected() {
    return misdirected;
  }

  /**
   * @return True if the cargo has been unloaded at the final destination.
   */
  public boolean isUnloadedAtDestination() {
    return isUnloadedAtDestination;
  }

  /**
   * @return Routing status.
   */
  public RoutingStatus routingStatus() {
    return routingStatus;
  }

  /**
   * @return When this delivery was calculated.
   */
  public Date calculatedAt() {
    return new Date(calculatedAt.getTime());
  }

  /**
   * @return True if the cargo is routed and not misdirected
   */
  boolean onTrack() {
    return routingStatus().sameValueAs(ROUTED) && !isMisdirected();
  }

  private static boolean misdirectionStatus(Itinerary itinerary, HandlingActivity handlingActivity) {
    return handlingActivity != null &&
           handlingActivity.type().isPhysical() &&
           !itinerary.isExpected(handlingActivity);
  }

  // TODO name this: "arrived" or something
  private static boolean unloadedAtDestination(RouteSpecification routeSpecification, HandlingActivity handlingActivity) {
    return handlingActivity != null &&
          (CLAIM.sameValueAs(handlingActivity.type()) || UNLOAD.sameValueAs(handlingActivity.type()) &&
           routeSpecification.destination().sameIdentityAs(handlingActivity.location()));
  }

  @Override
  public boolean sameValueAs(final Delivery other) {
    return other != null && new EqualsBuilder().
      append(this.mostRecentHandlingActivity, other.mostRecentHandlingActivity).
      append(this.misdirected, other.misdirected).
      append(this.isUnloadedAtDestination, other.isUnloadedAtDestination).
      append(this.routingStatus, other.routingStatus).
      append(this.calculatedAt, other.calculatedAt).
      isEquals();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Delivery other = (Delivery) o;

    return sameValueAs(other);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(mostRecentHandlingActivity).
      append(misdirected).
      append(isUnloadedAtDestination).
      append(routingStatus).
      append(calculatedAt).
      toHashCode();
  }

  Delivery() {
    // Needed by Hibernate
  }

}
