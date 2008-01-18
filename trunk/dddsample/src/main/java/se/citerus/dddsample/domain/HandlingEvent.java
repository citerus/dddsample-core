package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

/**
 * HandlingEvent links the type of handling with a CarrierMovement.
 * 
 * Since HandlingEvents can be added in any order to a Cargo (or
 * DeliveryHistory), they need to implement Comparable to be able to be sorted
 * in correct order.
 *
 */
@Entity
public class HandlingEvent {
  /**
   * Comparator used to be able to sort HandlingEvents according to their completion time
   */
  public static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR = new Comparator<HandlingEvent>() {
    public int compare(HandlingEvent o1, HandlingEvent o2) {
      return o1.completionTime().compareTo(o2.completionTime());
    }
  };

  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private Type type;

  @ManyToOne
  private CarrierMovement carrierMovement;

  @ManyToOne
  private Location location;

  private Date completionTime;

  private Date registrationTime;

  @ManyToOne
  @JoinColumn(name = "cargo_id")
  private Cargo cargo;

  public enum Type {
    LOAD, UNLOAD, RECEIVE, CLAIM, CUSTOMS
  }

  private HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type) {
    this.registrationTime = registrationTime;
    this.completionTime = completionTime;
    this.type = type;
    this.cargo = cargo;
  }

  /**
   * Constructor for events that do not have a carrier movement associated.
   *
   * @param cargo cargo
   * @param completionTime completion time
   * @param registrationTime registration time
   * @param type type of event. Legal values are CLAIM, RECIEVE and CUSTOMS
   * @param location where the event took place
   */
  public HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type, Location location) {
    this(cargo, completionTime, registrationTime, type);
    this.location = location;
  }

  /**
   * Constructor for events that have a carrier movement associated. The location where
   * the event took place is derived from the carrier movement: if the type of event is LOAD,
   * the location is the starting point of the movement, if the type is UNLOAD the location
   * is the end point.
   *
   * @param cargo cargo
   * @param completionTime completion time
   * @param registrationTime registration time
   * @param type type of event. Legal values are LOAD and UNLOAD
   * @param carrierMovement carrier movement.
   */
  public HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type, CarrierMovement carrierMovement) {
    this(cargo, completionTime, registrationTime, type);
    this.carrierMovement = carrierMovement;
    if (Type.LOAD.equals(type)) {
      this.location = carrierMovement.from();
    } else if (Type.UNLOAD.equals(type)) {
      this.location = carrierMovement.to();
    } else {
      throw new IllegalArgumentException("Can't derive location from carrier movement for event type " + type);
    }
  }

  public Long id() {
    return this.id;
  }

  public Type type() {
    return this.type;
  }

  public CarrierMovement carrierMovement() {
    return this.carrierMovement;
  }

  public Date completionTime() {
    return this.completionTime;
  }

  public Date registrationTime() {
    return this.registrationTime;
  }

  public Location location() {
    return this.location;
  }

  public Cargo cargo() {
    return this.cargo;
  }

  /**
   * @param object to compare
   * @return True if location, completion time and type are equal.
   */
  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (!(object instanceof HandlingEvent)) {
      return false;
    }
    HandlingEvent other = (HandlingEvent) object;
    return this.location.equals(other.location) &&
           this.completionTime.equals(other.completionTime) &&
           this.type.equals(other.type);
  }

  /**
   * @return Hash code calculated from the same properties as equals().
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(7, 39).
            append(this.location).
            append(this.completionTime).
            append(this.type).
            toHashCode();
  }

  /**
   * @param other to compare
   * @return True if the ids are equal.
   */
  public boolean sameIdentityAs(HandlingEvent other) {
    return other != null && id.equals(other.id);
  }

  // Needed by Hibernate
  HandlingEvent() {}

}
