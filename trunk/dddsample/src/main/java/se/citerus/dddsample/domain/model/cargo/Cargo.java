package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.Entity;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;

import java.util.Date;
import java.util.Iterator;

/**
 * A Cargo. This is the central class in the domain model,
 * and it is the root of the Cargo-Itinerary-Leg-Delivery-RouteSpecification aggregate.
 *
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a route specification. The life cycle of a cargo begins with the booking procedure,
 * when the tracking id is assigned. During a (short) period of time, between booking
 * and initial routing, the cargo has no itinerary.
 *
 * The booking clerk requests a list of possible routes, matching the route specification,
 * and assigns the cargo to one route. The route to which a cargo is assigned is described
 * by an itinerary.
 *
 * A cargo can be re-routed during transport, on demand of the customer, in which case
 * a new route is specified for the cargo and a new route is requested. The old itinerary,
 * being a value object, is discarded and a new one is attached.
 *
 * It may also happen that a cargo is accidentally misrouted, which should notify the proper
 * personnel and also trigger a re-routing procedure.
 *
 * The life cycle of a cargo ends when the cargo is claimed by the customer.
 *
 * The cargo aggregate, and the entre domain model, is built to solve the problem
 * of booking and tracking cargo. All important business rules for determining whether
 * or not a cargo is misrouted, what the current status of the cargo is (on board carrier,
 * in port etc), are captured in this aggregate.
 *
 */
public class Cargo implements Entity<Cargo> {

  private TrackingId trackingId;
  private Location origin;
  private Itinerary itinerary;
  private Delivery delivery;
  private RouteSpecification routeSpecification;
  private RoutingStatus routingStatus;
  private HandlingActivity nextExpectedActivity;
  private boolean misdirected;
  private Date eta;
  
  private static final Date ETA_UNKOWN = null;
  private static final HandlingActivity NO_ACTIVITY = null;

  public Cargo(final TrackingId trackingId, final RouteSpecification routeSpecification) {
    Validate.notNull(trackingId, "Tracking id is required");
    Validate.notNull(routeSpecification, "Route specification is required");

    this.trackingId = trackingId;
    // Cargo origin never changes, even if the route specification changes.
    // However, at creation, cargo orgin can be derived from the initial route specification.
    this.origin = routeSpecification.origin();
    this.routeSpecification = routeSpecification;

    deriveDeliveryProgress(HandlingHistory.EMPTY);
  }

  /**
   * The tracking id is the identity of this entity, and is unique.
   * 
   * @return Tracking id.
   */
  public TrackingId trackingId() {
    return this.trackingId;
  }

  /**
   * @return Origin location.
   */
  public Location origin() {
    return origin;
  }

  /**
   * @return The delivery. Never null.
   */
  public Delivery delivery() {
    return DomainObjectUtils.nullSafe(this.delivery, Delivery.EMPTY_DELIVERY);
  }

  /**
   * @return The itinerary. Never null.
   */
  public Itinerary itinerary() {
    return DomainObjectUtils.nullSafe(this.itinerary, Itinerary.EMPTY_ITINERARY);
  }

  /**
   * @return The route specification.
   */
  public RouteSpecification routeSpecification() {
    return routeSpecification;
  }
  
  /**
   * Specifies a new route for this cargo.
   *
   * @param routeSpecification route specification.
   */
  public void specifyNewRoute(final RouteSpecification routeSpecification) {
    Validate.notNull(routeSpecification, "Route specification is required");

    this.routeSpecification = routeSpecification;
    // Handling consistency within the Cargo aggregate synchronously
    this.routingStatus = deriveRoutingStatus();
  }

  /**
   * Attach a new itinerary to this cargo.
   *
   * @param itinerary an itinerary. May not be null.
   */
  public void assignToRoute(final Itinerary itinerary) {
    Validate.notNull(itinerary, "Itinerary is required for assignment");

    this.itinerary = itinerary;
    // Handling consistency within the Cargo aggregate synchronously
    this.routingStatus = deriveRoutingStatus();
    this.misdirected = deriveMisdirectionStatus();
    this.nextExpectedActivity = deriveNextExpectedActivity();
    this.eta = deriveEta();
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
   * @return Routing status.
   */
  public RoutingStatus routingStatus() {
    return routingStatus;
  }

  /**
   * @return True if the cargo has been unloaded at the final destination.
   */
  public boolean isUnloadedAtDestination() {
    final HandlingEvent lastEvent = delivery.lastEvent();
    return lastEvent != null &&
      UNLOAD.sameValueAs(lastEvent.type()) &&
      routeSpecification.destination().sameIdentityAs(lastEvent.location());
  }

  /**
   * @return estimated time of arrival
   */
  public Date estimatedTimeOfArrival() {
    if (eta != ETA_UNKOWN) {
      return new Date(eta.getTime());
    } else {
      return ETA_UNKOWN;
    }
  }

  /**
   * @return the next expected activity
   */
  public HandlingActivity nextExpectedActivity() {
    return nextExpectedActivity;
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
   * but changes to the delivery history (when a cargo is handled) cause the status update
   * to happen <b>asynchronously</b> since {@link HandlingEvent} is in a different aggregate.
   *
   * @param handlingHistory delivery history
   */
  public void deriveDeliveryProgress(final HandlingHistory handlingHistory) {
    // Delivery is a value object, so we can simply discard the old one
    // and replace it with a new
    this.delivery = Delivery.derivedFrom(handlingHistory);
    this.routingStatus = deriveRoutingStatus();
    this.misdirected = deriveMisdirectionStatus();
    this.eta = deriveEta();
    this.nextExpectedActivity = deriveNextExpectedActivity();
  }

  /**
   *
   * @return true if this cargo is misdirected.
   */
  private boolean deriveMisdirectionStatus() {
    final HandlingEvent lastEvent = delivery().lastEvent();
    if (lastEvent == null) {
      return false;
    } else {
      return !itinerary().isExpected(lastEvent);
    }
  }

  /**
   * @return current routing status
   */
  private RoutingStatus deriveRoutingStatus() {
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
   * @return estimated time of arrival, or null if unknown
   */
  private Date deriveEta() {
    if (onTrack()) {
      return itinerary().finalArrivalDate();
    } else {
      return ETA_UNKOWN;
    }
  }

  private HandlingActivity deriveNextExpectedActivity() {
    if (!onTrack()) return NO_ACTIVITY;

    final HandlingEvent lastEvent = delivery().lastEvent();

    if (lastEvent == null) return new HandlingActivity(RECEIVE, origin());

    switch (lastEvent.type()) {

      case LOAD:
        for (Leg leg : itinerary().legs()) {
          if (leg.loadLocation().sameIdentityAs(lastEvent.location())) {
            return new HandlingActivity(UNLOAD, leg.unloadLocation(), leg.voyage());
          }
        }

        return NO_ACTIVITY;

      case UNLOAD:
        for (Iterator<Leg> it = itinerary().legs().iterator(); it.hasNext();) {
          final Leg leg = it.next();
          if (leg.unloadLocation().sameIdentityAs(lastEvent.location())) {
            if (it.hasNext()) {
              final Leg nextLeg = it.next();
              return new HandlingActivity(LOAD, nextLeg.loadLocation(), nextLeg.voyage());
            } else {
              return new HandlingActivity(CLAIM, leg.unloadLocation());
            }
          }
        }

        return NO_ACTIVITY;

      case RECEIVE:
        final Leg firstLeg = itinerary().legs().iterator().next();
        return new HandlingActivity(LOAD, firstLeg.loadLocation(), firstLeg.voyage());

      case CLAIM:
      default:
        return NO_ACTIVITY;
    }
  }

  /**
   * @return true if cargo is on track, i.e. routed and not misdirected
   */
  private boolean onTrack() {
    return routingStatus.equals(ROUTED) && !misdirected;
  }
  
  @Override
  public boolean sameIdentityAs(final Cargo other) {
    return other != null && trackingId.sameValueAs(other.trackingId);
  }

  /**
   * @param object to compare
   * @return True if they have the same identity
   * @see #sameIdentityAs(Cargo)
   */
  @Override
  public boolean equals(final Object object) {
    if (this == object) return true;
    if (object == null || getClass() != object.getClass()) return false;

    final Cargo other = (Cargo) object;
    return sameIdentityAs(other);
  }

  /**
   * @return Hash code of tracking id.
   */
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }

  @Override
  public String toString() {
    return trackingId.toString();
  }

  Cargo() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  private Long id;

}
