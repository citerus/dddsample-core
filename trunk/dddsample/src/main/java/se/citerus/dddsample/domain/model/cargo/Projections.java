package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Date;
import java.util.Iterator;

/**
 * These are projections about the future handling of the cargo,
 * when it will arrive and what the next step is.
 * <p/>
 * It is updated on routing changes as well as handling.
 */
class Projections implements ValueObject<Projections> {

  private Date estimatedTimeOfArrival;
  private HandlingActivity nextExpectedActivity;

  private static final Date ETA_UNKOWN = null;
  private static final HandlingActivity NO_ACTIVITY = null;

  Projections(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification) {
    this.estimatedTimeOfArrival = calculateEstimatedTimeOfArrival(delivery, itinerary, routeSpecification);
    this.nextExpectedActivity = calculateNextExpectedActivity(delivery, itinerary, routeSpecification);
  }

  /**
   * @return Estimated time of arrival, or null if not known.
   */
  Date estimatedTimeOfArrival() {
    if (estimatedTimeOfArrival != ETA_UNKOWN) {
      return new Date(estimatedTimeOfArrival.getTime());
    } else {
      return ETA_UNKOWN;
    }
  }

  /**
   * @return The next expected handling activity.
   */
  HandlingActivity nextExpectedActivity() {
    return nextExpectedActivity;
  }

  private Date calculateEstimatedTimeOfArrival(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification) {
    if (delivery.onTrack(itinerary, routeSpecification)) {
      return itinerary.finalUnloadTime();
    } else {
      return ETA_UNKOWN;
    }
  }

  private HandlingActivity calculateNextExpectedActivity(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification) {

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

  private HandlingActivity receiveInFirstLocation(final Itinerary itinerary) {
    final Leg leg = itinerary.firstLeg();
    return new HandlingActivity(RECEIVE, leg.loadLocation());
  }

  private HandlingActivity loadInFirstLocation(final Itinerary itinerary) {
    final Leg leg = itinerary.firstLeg();
    return new HandlingActivity(LOAD, leg.loadLocation(), leg.voyage());
  }

  private HandlingActivity loadOrClaimInNextLocation(final Itinerary itinerary, final Location activityLocation) {
    for (final Iterator<Leg> it = itinerary.legs().iterator(); it.hasNext();) {
      final Leg leg = it.next();
      if (leg.unloadLocation().sameAs(activityLocation)) {
        if (it.hasNext()) {
          final Leg nextLeg = it.next();

          //return leg.loadActivity(); { return new HandlingActivity(voyage, loadLocation); }

          //return HandlingActivity.loadOnto(nextLeg.voyage()).in(nextLeg.loadLocation());

          return new HandlingActivity(LOAD, nextLeg.loadLocation(), nextLeg.voyage());
        } else {
          return new HandlingActivity(CLAIM, leg.unloadLocation());
        }
      }
    }

    return NO_ACTIVITY;
  }

  private HandlingActivity unloadInNextLocation(final Itinerary itinerary, final Location activityLocation) {
    for (final Leg leg : itinerary.legs()) {
      if (leg.loadLocation().sameAs(activityLocation)) {
        return new HandlingActivity(UNLOAD, leg.unloadLocation(), leg.voyage());
      }
    }

    return NO_ACTIVITY;
  }


  @Override
  public boolean sameValueAs(final Projections other) {
    return other != null && new EqualsBuilder().
      append(this.estimatedTimeOfArrival, other.estimatedTimeOfArrival).
      append(this.nextExpectedActivity, other.nextExpectedActivity).
      isEquals();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Projections other = (Projections) o;
    return sameValueAs(other);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(estimatedTimeOfArrival).
      append(nextExpectedActivity).
      toHashCode();
  }

  Projections() {
    // Needed by Hibernate
  }

}
