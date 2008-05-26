package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;

import javax.persistence.*;

/**
 * A Cargo.
 */
@Entity
public final class Cargo {

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  private TrackingId trackingId;

  @ManyToOne
  private Location origin;

  @ManyToOne
  private Location destination;

  @Transient
  private DeliveryHistory deliveryHistory = DeliveryHistory.EMPTY_DELIVERY_HISTORY;

  @ManyToOne(cascade = CascadeType.ALL)
  private Itinerary itinerary;

  /**
   * Constructor.
   *
   * @param trackingId
   * @param origin
   * @param destination
   */
  public Cargo(final TrackingId trackingId, final Location origin, final Location destination) {
    Validate.noNullElements(new Object[]{trackingId, origin, destination});
    this.trackingId = trackingId;
    this.origin = origin;
    this.destination = destination;
  }

  /**
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

  public void setOrigin(final Location origin) {
    Validate.notNull(origin);
    this.origin = origin;
  }

  public void setDestination(final Location destination) {
    Validate.notNull(destination);
    this.destination = destination;
  }

  /**
   * @return Final destination.
   */
  public Location finalDestination() {
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
   * Assigns an itinerary to this cargo.
   *
   * @param itinerary an itinerary
   */
  public void setItinerary(final Itinerary itinerary) {
    Validate.notNull(itinerary);
    this.itinerary = itinerary;
  }

  public void removeItinerary() {
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
    final Location destination = finalDestination();
    if (destination == null) {
      return false;
    }
    for (HandlingEvent event : deliveryHistory.eventsOrderedByCompletionTime()) {
      if (HandlingEvent.Type.UNLOAD.equals(event.type())
        && destination.equals(event.location())) {
        return true;
      }
    }
    return false;
  }

  /**
   * Entities compare by identity, therefore the trackingId field is the only basis of comparison. For persistence we
   * have an id field, but it is not used for identiy comparison.
   * <p/>
   * Compare this behavior to the value object {@link se.citerus.dddsample.domain.Leg#sameValueAs(Leg)}
   *
   * @param other The other cargo.
   * @return <code>true</code> if the given cargo's and this cargos's trackingId is the same, regardles of other
   *         attributes.
   */
  private boolean sameIdentityAs(final Cargo other) {
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
}
