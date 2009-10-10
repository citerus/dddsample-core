package se.citerus.dddsample.tracking.core.domain.model.cargo;

import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Date;
import java.util.Iterator;

/**
 * These are projections about the future handling of the cargo,
 * when it will arrive and what the next step is.
 * <p/>
 * It is updated on routing changes as well as handling.
 */
class Projections extends ValueObjectSupport<Projections> {

  private static final Date ETA_UNKOWN = null;
  private static final HandlingActivity NO_ACTIVITY = null;

  /**
   * @param delivery delivery
   * @param itinerary itinerary
   * @param routeSpecification routeSpecification
   * @return Estimated time of arrival, or null if not known.
   */
  static Date estimatedTimeOfArrival(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification) {
    if (delivery.onTrack(itinerary, routeSpecification)) {
      return new Date(itinerary.finalUnloadTime().getTime());
    } else {
      return ETA_UNKOWN;
    }
  }

  /**
   * @param delivery delivery
   * @param itinerary itinerary
   * @param routeSpecification routeSpecification 
   * @return The next expected handling activity.
   */
  static HandlingActivity nextExpectedActivity(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification) {
    /*
     TODO Capture:

     Cargo is misdirected but has been rerouted. Next expected acivity should be to load according to first leg
     of new itinerary.

     and

     even if a cargo is misdirected, we expect it to be unloaded at next stop.

    */
    if (!delivery.onTrack(itinerary, routeSpecification)) {
      return NO_ACTIVITY;
    }

    final Location lastKnownLocation = delivery.lastKnownLocation();
    switch (delivery.transportStatus()) {
      case IN_PORT:
        if (itinerary.firstLeg().loadLocation().sameAs(lastKnownLocation)) {
          return loadInFirstLocation(itinerary);
        } else {
          return loadOrClaimInNextLocation(itinerary, lastKnownLocation);
        }

      case NOT_RECEIVED:
        return receiveInFirstLocation(itinerary);

      case ONBOARD_CARRIER:
        return unloadInNextLocation(itinerary, lastKnownLocation);

      case CLAIMED:
      default:
        return NO_ACTIVITY;
    }
  }

  private static HandlingActivity receiveInFirstLocation(final Itinerary itinerary) {
    final Leg leg = itinerary.firstLeg();
    return new HandlingActivity(RECEIVE, leg.loadLocation());
  }

  private static HandlingActivity loadInFirstLocation(final Itinerary itinerary) {
    final Leg leg = itinerary.firstLeg();
    return new HandlingActivity(LOAD, leg.loadLocation(), leg.voyage());
  }

  private static HandlingActivity loadOrClaimInNextLocation(final Itinerary itinerary, final Location activityLocation) {
    for (final Iterator<Leg> it = itinerary.legs().iterator(); it.hasNext();) {
      final Leg leg = it.next();
      if (leg.unloadLocation().sameAs(activityLocation)) {
        if (it.hasNext()) {
          final Leg nextLeg = it.next();
          return new HandlingActivity(LOAD, nextLeg.loadLocation(), nextLeg.voyage());
        } else {
          return new HandlingActivity(CLAIM, leg.unloadLocation());
        }
      }
    }

    return NO_ACTIVITY;
  }

  private static HandlingActivity unloadInNextLocation(final Itinerary itinerary, final Location activityLocation) {
    for (final Leg leg : itinerary.legs()) {
      if (leg.loadLocation().sameAs(activityLocation)) {
        return new HandlingActivity(UNLOAD, leg.unloadLocation(), leg.voyage());
      }
    }

    return NO_ACTIVITY;
  }

  Projections() {
    // Needed by Hibernate
  }

}
