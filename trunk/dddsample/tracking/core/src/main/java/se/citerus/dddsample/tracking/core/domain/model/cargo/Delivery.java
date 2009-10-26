package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.ONBOARD_CARRIER;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Date;

/**
 * Everything about the delivery of the cargo, i.e. where the cargo is
 * right now, whether or not it's routed, misdirected and so on.
 */
class Delivery extends ValueObjectSupport<Delivery> {

  private final HandlingActivity mostRecentHandlingActivity;
  private final Date calculatedAt;

  /**
   * Derives a new delivery when a cargo has been handled.
   *
   * @param handlingActivity  handling activity
   * @param completionTime
   * @return An up to date delivery
   */
  static Delivery cargoWasHandled(final HandlingActivity handlingActivity, Date completionTime) {
    Validate.notNull(handlingActivity, "Handling activity is required");

    return new Delivery(handlingActivity, completionTime);
  }

  /**
   * @return Initial delivery, before any handling has taken place
   */
  static Delivery initial() {
    return new Delivery(null, new Date(0L));
  }

  Delivery(final HandlingActivity mostRecentHandlingActivity, final Date completionTime) {
    this.mostRecentHandlingActivity = mostRecentHandlingActivity;
    this.calculatedAt = completionTime;
  }

  HandlingActivity mostRecentHandlingActivity() {
    return mostRecentHandlingActivity;
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
    if (hasBeenHandled()) {
      return mostRecentHandlingActivity.location();
    } else {
      return Location.UNKNOWN;
    }
  }

  /**
   * @return Current voyage.
   */
  Voyage currentVoyage() {
    if (hasBeenHandled() && transportStatus() == ONBOARD_CARRIER) {
      return mostRecentHandlingActivity.voyage();
    } else {
      return Voyage.NONE;
    }
  }

  private boolean hasBeenHandled() {
    return mostRecentHandlingActivity != null;
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
    if (!hasBeenHandled()) {
      return false;
    }

    if (mostRecentHandlingActivity.type() == CUSTOMS) {
      boolean handledAtDestination = routeSpecification.destination().sameAs(mostRecentHandlingActivity.location());
      return !handledAtDestination;
    } else {
      return !itinerary.wasExpecting(mostRecentHandlingActivity);
    }
  }

  /**
   * @return True if the cargo has been unloaded at the final destination.
   * @param routeSpecification route specification
   */
  boolean isUnloadedAtDestination(final RouteSpecification routeSpecification) {
    if (hasBeenHandled()) {
      return (mostRecentHandlingActivity.type() == CLAIM ||
              mostRecentHandlingActivity.type() == UNLOAD && routeSpecification.destination().sameAs(mostRecentHandlingActivity.location()));
    } else {
      return false;
    }
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
  Date lastTimestamp() {
    return new Date(calculatedAt.getTime());
  }

  /**
   * @return True if the cargo is routed and not misdirected  @param itinerary
   * @param itinerary itinerary
   * @param routeSpecification route specification
   */
  boolean onTrack(final Itinerary itinerary, final RouteSpecification routeSpecification) {
    return routingStatus(itinerary, routeSpecification) == ROUTED && !isMisdirected(itinerary, routeSpecification);
  }

  Delivery() {
    // Needed by Hibernate
    calculatedAt = null;
    mostRecentHandlingActivity = null;
  }

}
