package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Date;

import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.ONBOARD_CARRIER;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.UNLOAD;

/**
 * Everything about the delivery of the cargo, i.e. where the cargo is
 * right now, whether or not it's routed, misdirected and so on.
 */
class Delivery extends ValueObjectSupport<Delivery> {

  private final HandlingActivity mostRecentHandlingActivity;
  private final HandlingActivity mostRecentPhysicalHandlingActivity;
  private final Date lastUpdatedOn;

  /**
   * @return Initial delivery, before any handling has taken place
   */
  static Delivery beforeHandling() {
    return new Delivery(null, null);
  }

  /**
   * Derives a new delivery when a cargo has been handled.
   *
   * @param newHandlingActivity  handling activity
   * @return An up to date delivery
   */
  Delivery onHandling(final HandlingActivity newHandlingActivity) {
    Validate.notNull(newHandlingActivity, "Handling activity is required");

    if (newHandlingActivity.type().isPhysical()) {
      return new Delivery(newHandlingActivity, newHandlingActivity);
    } else {
      return new Delivery(newHandlingActivity, mostRecentPhysicalHandlingActivity);
    }
  }

  /**
   * @return An up to date delivery
   */
  Delivery onRouting() {
    return new Delivery(mostRecentHandlingActivity, mostRecentPhysicalHandlingActivity);
  }

  private Delivery(final HandlingActivity mostRecentHandlingActivity,
                   final HandlingActivity mostRecentPhysicalHandlingActivity) {
    this.mostRecentHandlingActivity = mostRecentHandlingActivity;
    this.mostRecentPhysicalHandlingActivity = mostRecentPhysicalHandlingActivity;
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
    if (hasBeenHandled() && transportStatus() == ONBOARD_CARRIER) {
      return mostRecentHandlingActivity.voyage();
    } else {
      return Voyage.NONE;
    }
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
    return hasBeenHandled() && !itinerary.isExpectedActivity(mostRecentPhysicalHandlingActivity);
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

  Delivery() {
    // Needed by Hibernate
    lastUpdatedOn = null;
    mostRecentHandlingActivity = mostRecentPhysicalHandlingActivity = null;
  }

  boolean isUnloadedIn(Location customsClearancePoint) {
    return hasBeenHandled() &&
           mostRecentHandlingActivity.location().sameAs(customsClearancePoint) &&
           mostRecentHandlingActivity().type() == UNLOAD;
  }
}
