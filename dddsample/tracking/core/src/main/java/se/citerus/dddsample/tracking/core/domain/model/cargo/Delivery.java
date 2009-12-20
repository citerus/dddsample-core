package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.ONBOARD_CARRIER;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.UNLOAD;
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
  private final HandlingActivity mostRecentPhysicalHandlingActivity;
  private final Date lastUpdatedOn;
  private final boolean routedAfterHandling;

  /**
   * Derives a new delivery when a cargo has been handled.
   *
   * @param newHandlingActivity  handling activity
   * @return An up to date delivery
   */
  Delivery onHandling(final HandlingActivity newHandlingActivity) {
    Validate.notNull(newHandlingActivity, "Handling activity is required");

    if (newHandlingActivity.type().isPhysical()) {
      return new Delivery(newHandlingActivity, newHandlingActivity, false);
    } else {
      return new Delivery(newHandlingActivity, mostRecentPhysicalHandlingActivity, false);
    }
  }

  /**
   * @return An up to date delivery
   */
  Delivery onRouting() {
    return new Delivery(mostRecentHandlingActivity, mostRecentPhysicalHandlingActivity, true);
  }

  /**
   * @return Initial delivery, before any handling has taken place
   */
  static Delivery beforeHandling() {
    return new Delivery(null, null, false);
  }

  private Delivery(final HandlingActivity mostRecentHandlingActivity,
                   final HandlingActivity mostRecentPhysicalHandlingActivity,
                   final boolean routedAfterHandling) {
    this.mostRecentHandlingActivity = mostRecentHandlingActivity;
    this.mostRecentPhysicalHandlingActivity = mostRecentPhysicalHandlingActivity;
    this.routedAfterHandling = routedAfterHandling;
    this.lastUpdatedOn = new Date();
  }

  HandlingActivity mostRecentHandlingActivity() {
    return mostRecentHandlingActivity;
  }

  HandlingActivity mostRecentPhysicalHandlingActivity() {
    return mostRecentPhysicalHandlingActivity;
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
      return Location.NONE;
    }
  }

  /**
   * @return Current voyage.
   */
  Voyage currentVoyage() {
    if (hasBeenHandledAfterRouting() && transportStatus() == ONBOARD_CARRIER) {
      return mostRecentHandlingActivity.voyage();
    } else {
      return Voyage.NONE;
    }
  }

  /**
   * @return True if the cargo has been handled at least once since it was last routed
   */
  boolean hasBeenHandledAfterRouting() {
    return hasBeenHandled() && !routedAfterHandling;
  }

  /**
   * @return True if the cargo has been handled at least once
   */
  boolean hasBeenHandled() {
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
   * @param itinerary itinerary
   * @return <code>true</code> if the cargo has been misdirected.
   */
  boolean isMisdirected(final Itinerary itinerary) {
    return hasBeenHandledAfterRouting() && !itinerary.isExpectedActivity(mostRecentPhysicalHandlingActivity);
  }

  /**
   * @return True if the cargo has been unloaded at the final destination.
   * @param routeSpecification route specification
   */
  boolean onTheGroundAtDestination(final RouteSpecification routeSpecification) {
    return hasBeenHandledAfterRouting() &&
           mostRecentHandlingActivity.type() == UNLOAD &&
           routeSpecification.destination().sameAs(mostRecentHandlingActivity.location());
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
  Date lastUpdatedOn() {
    return new Date(lastUpdatedOn.getTime());
  }

  /**
   * @param itinerary itinerary
   * @param routeSpecification route specification
   * @return True if the cargo is routed and not misdirected
   */
  boolean isOnRoute(final Itinerary itinerary, final RouteSpecification routeSpecification) {
    return routingStatus(itinerary, routeSpecification) == ROUTED && !isMisdirected(itinerary);
  }

  /**
   * @return True if cargo has been routed after the most recent handling activity took place.
   */
  boolean isRoutedAfterHandling() {
    return routedAfterHandling;
  }

  Delivery() {
    // Needed by Hibernate
    lastUpdatedOn = null;
    mostRecentHandlingActivity = mostRecentPhysicalHandlingActivity = null;
    routedAfterHandling = false;
  }

}
