package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;


/**
 * A Cargo.
 */
@Entity
public class Cargo {

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
  private DeliveryHistory deliveryHistory = new DeliveryHistory();

  @Transient
  private Itinerary itinerary;

  //TODO Remove this constructor
  public Cargo(TrackingId trackingId, Location origin, Location destination) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.destination = destination;
  }

  public Cargo(TrackingId trackingId) {
    this.trackingId = trackingId;
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

  public void setOrigin(Location origin) {
    this.origin = origin;
  }

  public void setDestination(Location destination) {
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
   * @return Last known location of the cargo, or Location.UNKNOWN if the delivery history is empty.
   */
  public Location lastKnownLocation() {
    HandlingEvent lastEvent = deliveryHistory.lastEvent();
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
  public void assignItinerary(Itinerary itinerary) {
    this.itinerary = itinerary;
  }

  /**
   * @return True if the cargo has been misdirected,
   *         that is if the cargo is in a location that's not in the itinerary.
   */
  public boolean isMisdirected() {
    return !itinerary.isExpected(deliveryHistory.lastEvent());
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  /**
   * Entities compare by identity, therefore the trackingId field is the only basis of comparison. For persistence we
   * have an id field, but it is not used for identiy comparison.
   *
   * Compare this behavior to the value object {@link se.citerus.dddsample.domain.Leg#sameValueAs(Leg)}
   *
   * @param other The other cargo.
   * @return <code>true</code> if the given cargo's and this cargos's trackingId is the same, regardles of other
   * attributes.
   */
  private boolean sameIdentityAs(Cargo other) {
    return trackingId.equals(other.trackingId);
  }

  /**
   * @param object to compare
   * @return True if tracking ids are equal.
   */
  @Override
  public boolean equals(Object object) {
    if (!(object instanceof Cargo)) {
      return false;
    }
    Cargo other = (Cargo) object;
    return sameIdentityAs(other);
  }

  /**
   * @return Hash code of tracking id.
   */
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }

  // Needed by Hibernate
  protected Cargo() {
  }
}
