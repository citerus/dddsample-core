package se.citerus.dddsample.domain.model.cargo;

import se.citerus.dddsample.domain.shared.ValueObject;
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.ROUTED;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * These are projections about the future handling of the cargo,
 * when it will arrive and what the next step is.
 *
 * It is updated on routing changes as well as handling.
 *
 */
public class Projections implements ValueObject<Projections> {

    private Date estimatedTimeOfArrival;
    private HandlingActivity nextExpectedActivity;

    private static final Date ETA_UNKOWN = null;
    private static final HandlingActivity NO_ACTIVITY = null;

    Projections(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification) {
        this(delivery, itinerary, routeSpecification, null);
    }

    Projections(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification, final HandlingActivity handlingActivity) {
        this.estimatedTimeOfArrival = calculateEstimatedTimeOfArrival(delivery, itinerary);
        this.nextExpectedActivity = calculateNextExpectedActivity(delivery, itinerary, routeSpecification, handlingActivity);
    }

    Projections(final Date estimatedTimeOfArrival, final HandlingActivity nextExpectedActivity) {
        this.estimatedTimeOfArrival = estimatedTimeOfArrival;
        this.nextExpectedActivity = nextExpectedActivity;
    }

    /**
     * @return Estimated time of arrival, or null if not known.
     */
    public Date estimatedTimeOfArrival() {
        if (estimatedTimeOfArrival != ETA_UNKOWN) {
            return new Date(estimatedTimeOfArrival.getTime());
        } else {
            return ETA_UNKOWN;
        }
    }

    /**
     * @return The next expected handling activity.
     */
    public HandlingActivity nextExpectedActivity() {
        return nextExpectedActivity;
    }

    private Date calculateEstimatedTimeOfArrival(final Delivery delivery, final Itinerary itinerary) {
        if (onTrack(delivery.routingStatus(), delivery.isMisdirected())) {
            return itinerary.finalUnloadTime();
        } else {
            return ETA_UNKOWN;
        }
    }

    private boolean onTrack(final RoutingStatus routingStatus, final boolean misdirected) {
        return routingStatus.sameValueAs(ROUTED) && !misdirected;
    }

    private HandlingActivity calculateNextExpectedActivity(final Delivery delivery, final Itinerary itinerary, final RouteSpecification routeSpecification, final HandlingActivity handlingActivity) {
        return calculateNextExpectedActivity(routeSpecification, itinerary, handlingActivity, delivery.routingStatus(), delivery.transportStatus(), delivery.lastKnownLocation(), delivery.currentVoyage(), delivery.isMisdirected());
    }

    private HandlingActivity calculateNextExpectedActivity(final RouteSpecification routeSpecification,
                                                           final Itinerary itinerary,
                                                           final HandlingActivity handlingActivity,
                                                           final RoutingStatus routingStatus,
                                                           final TransportStatus transportStatus,
                                                           final Location lastKnownLocation,
                                                           final Voyage currentVoyage,
                                                           final boolean misdirected) {
        /*
         Capture:

         Cargo is misdirected but has been rerouted. Next expected acivity should be to load according to first leg
         of new itinerary.

         and

         even if a cargo is misdirected, we expect it to be unloaded at next stop.

        */
        if (!onTrack(routingStatus, misdirected)) return NO_ACTIVITY;

        switch (transportStatus) {
            case IN_PORT:
                if (itinerary.firstLeg().loadLocation().sameIdentityAs(lastKnownLocation)) {
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

        /*
        switch (handlingActivity.type()) {
          case LOAD:
            return unloadInNextLocation(itinerary, handlingActivity);
          case UNLOAD:
            return loadOrClaimInNextLocation(itinerary, handlingActivity);
          case RECEIVE:
            return receiveInFirstLocation(itinerary);
          case CLAIM:
          default:
            return NO_ACTIVITY;
        }
        */
    }

    private HandlingActivity receiveInFirstLocation(final Itinerary itinerary) {
        final Leg leg = itinerary.firstLeg();
        return new HandlingActivity(HandlingEvent.Type.RECEIVE, leg.loadLocation());
    }

    private HandlingActivity loadInFirstLocation(final Itinerary itinerary) {
        final Leg leg = itinerary.firstLeg();
        return new HandlingActivity(HandlingEvent.Type.LOAD, leg.loadLocation(), leg.voyage());
    }

    private HandlingActivity loadOrClaimInNextLocation(final Itinerary itinerary, final Location activityLocation) {
        for (final Iterator<Leg> it = itinerary.legs().iterator(); it.hasNext();) {
            final Leg leg = it.next();
            if (leg.unloadLocation().sameIdentityAs(activityLocation)) {
                if (it.hasNext()) {
                    final Leg nextLeg = it.next();
                    return new HandlingActivity(HandlingEvent.Type.LOAD, nextLeg.loadLocation(), nextLeg.voyage());
                } else {
                    return new HandlingActivity(HandlingEvent.Type.CLAIM, leg.unloadLocation());
                }
            }
        }

        return NO_ACTIVITY;
    }

    private HandlingActivity unloadInNextLocation(final Itinerary itinerary, final Location activityLocation) {
        for (final Leg leg : itinerary.legs()) {
            if (leg.loadLocation().sameIdentityAs(activityLocation)) {
                return new HandlingActivity(HandlingEvent.Type.UNLOAD, leg.unloadLocation(), leg.voyage());
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
