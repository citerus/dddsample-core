package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.CustomsZone;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.entity.EntitySupport;

import java.util.Date;
import java.util.Iterator;

/**
 * A Cargo. This is the central class in the domain model,
 * and it is the root of the Cargo-Itinerary-Leg-Delivery-RouteSpecification aggregate.
 * <p/>
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a route specification. The life cycle of a cargo begins with the booking procedure,
 * when the tracking id is assigned. During a (short) period of time, between booking
 * and initial routing, the cargo has no itinerary.
 * <p/>
 * The booking clerk requests a list of possible routes, matching the route specification,
 * and assigns the cargo to one route. The route to which a cargo is assigned is described
 * by an itinerary.
 * <p/>
 * A cargo can be re-routed during transport, on demand of the customer, in which case
 * a new route is specified for the cargo and a new route is requested. The old itinerary,
 * being a value object, is discarded and a new one is attached.
 * <p/>
 * It may also happen that a cargo is accidentally misrouted, which should notify the proper
 * personnel and also trigger a re-routing procedure.
 * <p/>
 * When a cargo is handled, the status of the delivery changes. Everything about the delivery
 * of the cargo is contained in the Delivery value object, which is replaced whenever a cargo
 * is handled by an asynchronous event triggered by the registration of the handling event.
 * <p/>
 * The delivery can also be affected by routing changes, i.e. when a the route specification
 * changes, or the cargo is assigned to a new route. In that case, the delivery update is performed
 * synchronously within the cargo aggregate.
 * <p/>
 * The life cycle of a cargo ends when the cargo is claimed by the customer.
 * <p/>
 * The cargo aggregate, and the entre domain model, is built to solve the problem
 * of booking and tracking cargo. All important business rules for determining whether
 * or not a cargo is misdirected, what the current status of the cargo is (on board carrier,
 * in port etc), are captured in this aggregate.
 */
public class Cargo extends EntitySupport<Cargo,TrackingId> {

  private final TrackingId trackingId;
  private RouteSpecification routeSpecification;
  private Itinerary itinerary;
  private Delivery delivery;

  public Cargo(final TrackingId trackingId, final RouteSpecification routeSpecification) {
    Validate.notNull(trackingId, "Tracking ID is required");
    Validate.notNull(routeSpecification, "Route specification is required");

    this.trackingId = trackingId;
    this.routeSpecification = routeSpecification;
    this.delivery = Delivery.initial();
  }

  @Override
  public TrackingId identity() {
    return trackingId;
  }

  /**
   * The tracking id is the identity of this entity, and is unique.
   *
   * @return Tracking id.
   */
  public TrackingId trackingId() {
    return trackingId;
  }

  /**
   * @return The itinerary. Never null.
   */
  public Itinerary itinerary() {
    return itinerary;
  }

  /**
   * @return The route specification.
   */
  public RouteSpecification routeSpecification() {
    return routeSpecification;
  }

  /**
   * @return Estimated time of arrival.
   */
  public Date estimatedTimeOfArrival() {
    if (onTrack()) {
      return itinerary.estimatedTimeOfArrival();
    } else {
      return null;
    }
  }

  /**
   * @return Next expected activity.
   */
  public HandlingActivity nextExpectedActivity() {
    /*
     TODO Capture:

     Cargo is misdirected but has been rerouted. Next expected acivity should be to load according to first leg
     of new itinerary.

     and

     even if a cargo is misdirected, we expect it to be unloaded at next stop.

    */
    if (!onTrack()) {
      return null;
    }

    final Location lastKnownLocation = delivery.lastKnownLocation();

    switch (delivery.transportStatus()) {
      case IN_PORT:
        if (itinerary.firstLeg().loadLocation().sameAs(lastKnownLocation)) {
          final Leg firstLeg = itinerary.firstLeg();
          return new HandlingActivity(LOAD, firstLeg.loadLocation(), firstLeg.voyage());
        } else {
          for (Iterator<Leg> it = itinerary.legs().iterator(); it.hasNext();) {
            final Leg leg = it.next();
            if (leg.unloadLocation().sameAs(lastKnownLocation)) {
              if (it.hasNext()) {
                final Leg nextLeg = it.next();
                return new HandlingActivity(LOAD, nextLeg.loadLocation(), nextLeg.voyage());
              } else {
                return new HandlingActivity(CLAIM, leg.unloadLocation());
              }
            }
          }

          return null;
        }

      case NOT_RECEIVED:
        final Leg leg = itinerary.firstLeg();
        return new HandlingActivity(RECEIVE, leg.loadLocation());

      case ONBOARD_CARRIER:
        for (Leg leg1 : itinerary.legs()) {
          if (leg1.loadLocation().sameAs(lastKnownLocation)) {
            return new HandlingActivity(UNLOAD, leg1.unloadLocation(), leg1.voyage());
          }
        }

        return null;

      case CLAIMED:
      default:
        return null;
    }
  }

  /**
   * @return True if the cargo is assigned to a route and is following that route.
   */
  public boolean onTrack() {
    return delivery.onTrack(itinerary, routeSpecification);
  }

  /**
   * @return True if cargo is misdirected.
   */
  public boolean isMisdirected() {
    return delivery.isMisdirected(itinerary, routeSpecification);
  }

  /**
   * @return Transport status.
   */
  public TransportStatus transportStatus() {
    return delivery.transportStatus();
  }

  /**
   * @return Routing status.
   */
  public RoutingStatus routingStatus() {
    return delivery.routingStatus(itinerary, routeSpecification);
  }

  /**
   * @return Current voyage.
   */
  public Voyage currentVoyage() {
    return delivery.currentVoyage();
  }

  /**
   * @return Last known location.
   */
  public Location lastKnownLocation() {
    return delivery.lastKnownLocation();
  }

  /**
   * Specifies a new route for this cargo.
   *
   * @param routeSpecification route specification.
   */
  public void specifyNewRoute(final RouteSpecification routeSpecification) {
    Validate.notNull(routeSpecification, "Route specification is required");

    this.routeSpecification = routeSpecification;
  }

  /**
   * Attach a new itinerary to this cargo.
   *
   * @param itinerary an itinerary. May not be null.
   */
  public void assignToRoute(final Itinerary itinerary) {
    Validate.notNull(itinerary, "Itinerary is required");

    this.itinerary = itinerary;
  }

  /**
   * @return Customs zone.
   */
  public CustomsZone customsZone() {
    return routeSpecification.destination().customsZone();
  }


  /**
   * @return Customs clearance point.
   */
  public Location customsClearancePoint() {
    return customsZone().entryPoint(itinerary.locations());
  }

  /**
   * @return True if the cargo is ready to be claimed.
   */
  public boolean isReadyToClaim() {
    return delivery.isUnloadedAtDestination(routeSpecification);
  }

  /**
   * Updates all aspects of the cargo aggregate status
   * based on the current route specification, itinerary and handling of the cargo.
   * <p/>
   * When either of those three changes, i.e. when a new route is specified for the cargo,
   * the cargo is assigned to a route or when the cargo is handled, the status must be
   * re-calculated.
   * <p/>
   * {@link RouteSpecification} and {@link Itinerary} are both inside the Cargo
   * aggregate, so changes to them cause the status to be updated <b>synchronously</b>,
   * but handling cause the status update to happen <b>asynchronously</b>
   * since {@link HandlingEvent} is in a different aggregate.
   *
   * @param handlingActivity handling activity
   */
  public void handled(final HandlingActivity handlingActivity) {
    Validate.notNull(handlingActivity, "Handling activity is required");

    // Delivery is a value object, so it's replaced with a new one
    this.delivery = Delivery.whenHandled(handlingActivity);
  }

  @Override
  public String toString() {
    return trackingId + " (" + routeSpecification + ")";
  }

  Cargo() {
    // Needed by Hibernate
    trackingId = null;
  }

}
