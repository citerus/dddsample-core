package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.*;


/**
 * A Cargo.
 *
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
  private Location finalDestination;

  @Embedded
  private DeliveryHistory deliveryHistory;

  public Cargo(TrackingId trackingId, Location origin, Location finalDestination) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.deliveryHistory =  new DeliveryHistory();
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

  /**
   * @return Final destination.
   */
  public Location finalDestination() {
    return this.finalDestination;
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
    
    if (lastEvent == null) {
      return Location.UNKNOWN;
    }
    
    
    
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
    return finalDestination.equals(lastKnownLocation());
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
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
    return trackingId.equals(other.trackingId);
  }

  /**
   * @return Hash code of tracking id.
   */
  @Override
  public int hashCode() {
    return trackingId.hashCode();
  }

  // Needed by Hibernate
  Cargo() {}
}
