package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.ONBOARD_CARRIER;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
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

  /**
   * Derives a new delivery when a cargo has been handled.
   *
   * @param handlingActivity  handling activity
   * @return An up to date delivery
   */
  static Delivery whenHandled(final HandlingActivity handlingActivity) {
    Validate.notNull(handlingActivity, "Handling activity is required");

    return new Delivery(handlingActivity);
  }

  /**
   * @return Initial delivery, before any handling has taken place
   */
  static Delivery initial() {
    return new Delivery(null);
  }

  Delivery(final HandlingActivity mostRecentHandlingActivity) {
    this.mostRecentHandlingActivity = mostRecentHandlingActivity;
    this.calculatedAt = new Date();
  }

  /**
   * @return Transport status
   */
  TransportStatus transportStatus() {
    return TransportStatus.derivedFrom(mostRecentHandlingActivity);
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  Location lastKnownLocation() {
    if (mostRecentHandlingActivity != null) {
      return mostRecentHandlingActivity.location();
    } else {
      return Location.UNKNOWN;
    }
  }

  /**
   * @return Current voyage.
   */
  Voyage currentVoyage() {
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
   * @param itinerary itinerary
   * @param routeSpecification route specification
   */
  boolean isMisdirected(final Itinerary itinerary, final RouteSpecification routeSpecification) {
    if (mostRecentHandlingActivity == null) {
      return false;
    }

    if (mostRecentHandlingActivity.type().sameValueAs(HandlingEvent.Type.CUSTOMS)) {
      return !routeSpecification.destination().sameIdentityAs(mostRecentHandlingActivity.location());
    } else {
      return !itinerary.isExpected(mostRecentHandlingActivity);
    }
  }

  /**
   * @return True if the cargo has been unloaded at the final destination.
   * @param routeSpecification route specification
   */
  boolean isUnloadedAtDestination(final RouteSpecification routeSpecification) {
    return mostRecentHandlingActivity != null &&
          (CLAIM.sameValueAs(mostRecentHandlingActivity.type()) || UNLOAD.sameValueAs(mostRecentHandlingActivity.type()) &&
           routeSpecification.destination().sameIdentityAs(mostRecentHandlingActivity.location()));
  }

  /**
   * @return Routing status.
   * @param itinerary itinerary
   * @param routeSpecification route specification
   */
  RoutingStatus routingStatus(final Itinerary itinerary, final RouteSpecification routeSpecification) {
    if (itinerary == null) {
      return NOT_ROUTED;
    } else {
      if (routeSpecification.isSatisfiedBy(itinerary)) {
        return ROUTED;
      } else {
        return MISROUTED;
      }
    }
  }

  /**
   * @return When this delivery was calculated.
   */
  Date calculatedAt() {
    return new Date(calculatedAt.getTime());
  }

  /**
   * @return True if the cargo is routed and not misdirected  @param itinerary
   * @param itinerary itinerary
   * @param routeSpecification route specification
   */
  boolean onTrack(final Itinerary itinerary, final RouteSpecification routeSpecification) {
    return routingStatus(itinerary, routeSpecification).sameValueAs(ROUTED) && 
           !isMisdirected(itinerary, routeSpecification);
  }

  @Override
  public boolean sameValueAs(final Delivery other) {
    return other != null && new EqualsBuilder().
      append(this.mostRecentHandlingActivity, other.mostRecentHandlingActivity).
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
      append(calculatedAt).
      toHashCode();
  }

  Delivery() {
    // Needed by Hibernate
  }

}
