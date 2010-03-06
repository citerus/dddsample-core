package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.location.CustomsZone;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.entity.EntitySupport;

import java.util.Date;

import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.NOT_ROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.TransportStatus.ONBOARD_CARRIER;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.customsIn;

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
    this.delivery = Delivery.beforeHandling();
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
   * @return The itinerary.
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
    if (delivery.isOnRoute(itinerary, routeSpecification)) {
      return itinerary.estimatedTimeOfArrival();
    } else {
      return null;
    }
  }

  /**
   * @return Next expected activity. If the cargo is not on route (misdirected and/or misrouted),
   * it cannot be determined and null is returned.
   */
  public HandlingActivity nextExpectedActivity() {
    if (!delivery.isOnRoute(itinerary, routeSpecification)) {
      return null;
    }

    if (delivery.isUnloadedIn(customsClearancePoint())) {
      return customsIn(customsClearancePoint());
    } else {
      return itinerary.activitySucceding(delivery.mostRecentPhysicalHandlingActivity());
    }
  }

  /**
   * @return True if cargo is misdirected.
   */
  public boolean isMisdirected() {
    return delivery.isMisdirected(itinerary);
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

    if (succedsMostRecentActivity(handlingActivity)) {
      this.delivery = delivery.onHandling(handlingActivity);
    }
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

    if (routingStatus() != NOT_ROUTED) {
      this.delivery = delivery.onRouting();
    }
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
    if (itinerary == null) {
      return Location.NONE;
    } else {
      return customsZone().entryPoint(itinerary.locations());
    }
  }

  /**
   * @return True if the cargo is ready to be claimed.
   */
  public boolean isReadyToClaim() {
    if (customsClearancePoint().sameAs(routeSpecification.destination())) {
      return customsIn(customsClearancePoint()).sameValueAs(mostRecentHandlingActivity());
    } else {
      return delivery.isUnloadedIn(routeSpecification.destination());
    }
  }

  /**
   * @return Most recent handling activity, or null if never handled.
   */
  public HandlingActivity mostRecentHandlingActivity() {
    return delivery.mostRecentHandlingActivity();
  }

  /**
   * @return The earliest rerouting location.
   * If the cargo is in port, it's the current location.
   * If it's onboard a carrier it's the next arrival location.
   */
  public Location earliestReroutingLocation() {
    if (isMisdirected()) {
      if (transportStatus() == ONBOARD_CARRIER) {
        return currentVoyage().arrivalLocationAfterDepartureFrom(lastKnownLocation());
      } else {
        return lastKnownLocation();
      }
    } else {
      return itinerary.matchLeg(delivery.mostRecentPhysicalHandlingActivity()).leg().unloadLocation();
    }
  }

  /**
   * @param other itinerary
   * @return An merge between the current itinerary and the provided itinerary
   * that describes a continuous route even if the cargo is currently misdirected.
   */
  public Itinerary itineraryMergedWith(final Itinerary other) {
    if (this.itinerary == null) {
      return other;
    }

    if (isMisdirected() && transportStatus() == ONBOARD_CARRIER) {
      final Leg currentLeg = Leg.deriveLeg(
        currentVoyage(), lastKnownLocation(), currentVoyage().arrivalLocationAfterDepartureFrom(lastKnownLocation())
      );

      return this.itinerary().
        truncatedAfter(lastKnownLocation()).
        withLeg(currentLeg).
        appendBy(other);
    } else {
      return this.itinerary().
        truncatedAfter(earliestReroutingLocation()).
        appendBy(other);
    }
  }

  private boolean succedsMostRecentActivity(final HandlingActivity newHandlingActivity) {
    if (delivery.hasBeenHandled()) {
      final HandlingActivity priorActivity = itinerary.strictlyPriorOf(delivery.mostRecentPhysicalHandlingActivity(), newHandlingActivity);
      return !newHandlingActivity.sameValueAs(priorActivity);
    } else {
      return true;
    }
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
