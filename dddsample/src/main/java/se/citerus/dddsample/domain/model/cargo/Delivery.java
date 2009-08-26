package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Date;

/**
 * Everything about the delivery of the cargo, i.e. where the cargo is
 * right now, whether or not it's routed, misdirected and so on.
 */
public class Delivery implements ValueObject<Delivery> {

  private TransportStatus transportStatus;
  private Location lastKnownLocation;
  private Voyage currentVoyage;
  private boolean misdirected;
  private boolean isUnloadedAtDestination;
  private RoutingStatus routingStatus;
  private Date calculatedAt;

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

    final RoutingStatus newRoutingStatus = RoutingStatus.derivedFrom(itinerary, routeSpecification);
    boolean misdirected = false;

    return new Delivery(transportStatus, lastKnownLocation, currentVoyage, misdirected, isUnloadedAtDestination, newRoutingStatus);
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

    final RoutingStatus newRoutingStatus = this.routingStatus;
    final TransportStatus newTransportStatus = TransportStatus.derivedFrom(handlingActivity);
    final boolean newMisdirected = Calculate.misdirectionStatus(itinerary, handlingActivity);
    final Location newLastKnownLocation = Calculate.lastKnownLocation(handlingActivity);
    final Voyage newCurrentVoyage = Calculate.currentVoyage(handlingActivity, newTransportStatus);
    final boolean newUnloadedAtDestination = Calculate.unloadedAtDestination(routeSpecification, handlingActivity);

    return new Delivery(newTransportStatus, newLastKnownLocation, newCurrentVoyage, newMisdirected, newUnloadedAtDestination, newRoutingStatus);
  }

  /**
   *
   * @param routeSpecification
   * @param itinerary
   * @return
   */
  static Delivery initial(final RouteSpecification routeSpecification, final Itinerary itinerary) {
    Validate.notNull(routeSpecification, "Route specification is required");

    final TransportStatus newTransportStatus = TransportStatus.derivedFrom(null);
    final Location newLastKnownLocation = Calculate.lastKnownLocation(null);
    final Voyage newCurrentVoyage = Calculate.currentVoyage(null, newTransportStatus);
    final boolean newMisdirected = Calculate.misdirectionStatus(itinerary, null);
    final boolean newUnloadedAtDestination = Calculate.unloadedAtDestination(routeSpecification, null);
    final RoutingStatus newRoutingStatus = RoutingStatus.derivedFrom(itinerary, routeSpecification);

    return new Delivery(newTransportStatus, newLastKnownLocation, newCurrentVoyage, newMisdirected, newUnloadedAtDestination, newRoutingStatus);
  }

  private Delivery(final TransportStatus transportStatus,
                   final Location lastKnownLocation,
                   final Voyage currentVoyage,
                   final boolean misdirected,
                   final boolean unloadedAtDestination,
                   final RoutingStatus routingStatus) {
    this.transportStatus = transportStatus;
    this.lastKnownLocation = lastKnownLocation;
    this.currentVoyage = currentVoyage;
    this.misdirected = misdirected;
    this.isUnloadedAtDestination = unloadedAtDestination;
    this.routingStatus = routingStatus;
    this.calculatedAt = new Date();
  }

  /**
   * @return Transport status
   */
  public TransportStatus transportStatus() {
    return transportStatus;
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    return DomainObjectUtils.nullSafe(lastKnownLocation, Location.UNKNOWN);
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
    return DomainObjectUtils.nullSafe(currentVoyage, Voyage.NONE);
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

  @Override
  public boolean sameValueAs(final Delivery other) {
    return other != null && new EqualsBuilder().
      append(this.transportStatus, other.transportStatus).
      append(this.lastKnownLocation, other.lastKnownLocation).
      append(this.currentVoyage, other.currentVoyage).
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
      append(transportStatus).
      append(lastKnownLocation).
      append(currentVoyage).
      append(misdirected).
      append(isUnloadedAtDestination).
      append(routingStatus).
      append(calculatedAt).
      toHashCode();
  }

  Delivery() {
    // Needed by Hibernate
  }

  private static class Calculate {

    private static Location lastKnownLocation(HandlingActivity handlingActivity) {
      if (handlingActivity != null) {
        return handlingActivity.location();
      } else {
        return null;
      }
    }

    private static Voyage currentVoyage(HandlingActivity handlingActivity, TransportStatus transportStatus) {
      if (transportStatus.equals(ONBOARD_CARRIER) && handlingActivity != null) {
        return handlingActivity.voyage();
      } else {
        return null;
      }
    }

    private static boolean misdirectionStatus(Itinerary itinerary, HandlingActivity handlingActivity) {
      if (handlingActivity == null) {
        return false;
      } else {
        return !itinerary.isExpected(handlingActivity);
      }
    }

    private static boolean unloadedAtDestination(RouteSpecification routeSpecification, HandlingActivity handlingActivity) {
      return handlingActivity != null &&
        (HandlingEvent.Type.CLAIM.sameValueAs(handlingActivity.type()) ||
          HandlingEvent.Type.UNLOAD.sameValueAs(handlingActivity.type()) &&
            routeSpecification.destination().sameIdentityAs(handlingActivity.location()));
    }

  }

}
