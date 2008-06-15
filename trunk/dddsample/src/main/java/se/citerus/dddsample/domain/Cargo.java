package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;

/**
 * A Cargo.
 */
public final class Cargo implements Entity<Cargo> {

  private TrackingId trackingId;
  private Location origin;
  private Location destination;
  private Itinerary itinerary;
  private DeliveryHistory deliveryHistory = DeliveryHistory.EMPTY_DELIVERY_HISTORY;

  /**
   * @param trackingId tracking id
   * @param origin origin location
   * @param destination destination location
   */
  public Cargo(final TrackingId trackingId, final Location origin, final Location destination) {
    Validate.noNullElements(new Object[] {trackingId, origin, destination});

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
   * @param destination the new destination. May not be null.
   */
  public void setDestination(final Location destination) {
    Validate.notNull(destination);

    this.destination = destination;
  }

  /**
   * @return Final destination.
   */
  public Location destination() {
    return this.destination;
  }

  /**
   * @return Delivery history.
   */
  public DeliveryHistory deliveryHistory() {
    return this.deliveryHistory;
  }

  /**
   * @return The itinerary.
   */
  public Itinerary itinerary() {
    if (this.itinerary == null) {
      return Itinerary.EMPTY_ITINERARY;
    } else {
      return this.itinerary;
    }
  }

  /**
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    final HandlingEvent lastEvent = deliveryHistory.lastEvent();
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
  public void attachItinerary(final Itinerary itinerary) {
    Validate.notNull(itinerary);

    // Decouple the old itinerary from this cargo 
    itinerary().setCargo(null);
    // Couple this cargo and the new itinerary
    this.itinerary = itinerary;
    itinerary().setCargo(this);
  }

  /**
   * Detaches the current itinerary from the cargo.
   */
  public void detachItinerary() {
    itinerary().setCargo(null);
    this.itinerary = null;
  }

  /**
   * @param deliveryHistory Cargo delivery history
   */
  public void setDeliveryHistory(final DeliveryHistory deliveryHistory) {
    Validate.notNull(deliveryHistory);
    this.deliveryHistory = deliveryHistory;
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
    final HandlingEvent lastEvent = deliveryHistory.lastEvent();
    if (itinerary == null || lastEvent == null) {
      return false;
    } else {
      return !itinerary.isExpected(lastEvent);
    }
  }

  /**
   * Does not take into account the possibility of the cargo havin been
   * (errouneously) loaded onto another carrier after it has been unloaded
   * at the final destination.
   *
   * @return True if the cargo has been unloaded at the final destination.
   */
  public boolean isUnloadedAtDestination() {
    for (HandlingEvent event : deliveryHistory.eventsOrderedByCompletionTime()) {
      if (HandlingEvent.Type.UNLOAD.equals(event.type())
        && destination.equals(event.location())) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean sameIdentityAs(final Cargo other) {
    return other != null && trackingId.equals(other.trackingId);
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
