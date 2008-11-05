package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.Entity;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainObjectUtils;

/**
 * A Cargo. This is the central class in the domain model,
 * and it is the root of the Cargo-Itinerary-Leg-DeliveryHistory aggregate.
 *
 * A cargo is identified by a unique tracking id, and it always has an origin
 * and a destination. The life cycle of a cargo begins with the booking procedure,
 * when the tracking id is assigned. During a (short) period of time, between booking
 * and initial routing, the cargo has no itinerary.
 *
 * The booking clerk requests a list of possible routes, matching a route specification,
 * and assigns the cargo to one route. An itinerary listing the legs of the route
 * is attached to the cargo.
 *
 * A cargo can be re-routed during transport, on demand of the customer, in which case
 * the destination is changed and a new route is requested. The old itinerary,
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
public final class Cargo implements Entity<Cargo> {

  private TrackingId trackingId;
  private Location origin;
  private Location destination;
  private Itinerary itinerary;
  private Delivery delivery;

  /**
   * @param trackingId tracking id
   * @param origin origin location
   * @param destination destination location
   */
  public Cargo(final TrackingId trackingId, final Location origin, final Location destination) {
    Validate.notNull(trackingId);
    Validate.notNull(origin);
    Validate.notNull(destination);

    this.trackingId = trackingId;
    this.origin = origin;
    this.destination = destination;
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
    return this.origin;
  }

  /**
   * @param newDestination the new destination. May not be null.
   */
  public void changeDestination(final Location newDestination) {
    Validate.notNull(newDestination);

    this.destination = newDestination;
  }

  /**
   * @return Destination of the cargo.
   */
  public Location destination() {
    return this.destination;
  }

  /**
   * @return The delivery history. Never null.
   */
  public Delivery deliveryHistory() {
    return DomainObjectUtils.nullSafe(this.delivery, Delivery.EMPTY_DELIVERY);
  }

  /**
   * @return The itinerary. Never null.
   */
  public Itinerary itinerary() {
    return DomainObjectUtils.nullSafe(this.itinerary, Itinerary.EMPTY_ITINERARY);
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    final HandlingEvent lastEvent = deliveryHistory().lastEvent();
    if (lastEvent != null) {
      return lastEvent.location();
    } else {
      return Location.UNKNOWN;
    }
  }

  /**
   * @return True if the cargo has arrived at its final destination.
   */
  public boolean hasArrived() {
    return destination.equals(lastKnownLocation());
  }

  /**
   * Attach a new itinerary to this cargo.
   *
   * @param itinerary an itinerary. May not be null.
   */
  public void assignToRoute(final Itinerary itinerary) {
    Validate.notNull(itinerary);

    // Decouple the old itinerary (which may or may not exist) from this cargo
    itinerary().setCargo(null);

    // Couple this cargo and the new itinerary
    this.itinerary = itinerary;
    this.itinerary.setCargo(this);
  }

  /**
   * @param delivery Cargo delivery history
   */
  void setDeliveryHistory(final Delivery delivery) {
    Validate.notNull(delivery);
    this.delivery = delivery;
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
    final HandlingEvent lastEvent = deliveryHistory().lastEvent();
    if (lastEvent == null) {
      return false;
    } else {
      return !itinerary().isExpected(lastEvent);
    }
  }

  /**
   * Does not take into account the possibility of the cargo having been
   * (errouneously) loaded onto another carrier after it has been unloaded
   * at the final destination.
   *
   * @return True if the cargo has been unloaded at the final destination.
   */
  public boolean isUnloadedAtDestination() {
    for (HandlingEvent event : deliveryHistory().history()) {
      if (HandlingEvent.Type.UNLOAD.equals(event.type())
        && destination.equals(event.location())) {
        return true;
      }
    }
    return false;
  }

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
    if (!(object instanceof Cargo)) {
      return false;
    }
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

  Cargo() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  private Long id;

}
