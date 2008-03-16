package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Date;

/**
 * A HandlingEvent is used to register the event when, for instance, a cargo is unloaded from a carrier at a some loacation at a given time. The
 * HandlingEvent's are sent from different Incident Logging Applications some time after the event occured and contain information about the
 * {@link TrackingId}, {@link Location}, timestamp of the completion of the event, and possibly, if applicable a {@link CarrierMovement}.
 * <br><br>
 * HandlingEvent's could contain information about a {@link CarrierMovement} and if so, the event type must be either {@link Type#LOAD} or
 * {@link Type#UNLOAD}. All other events must be of {@link Type#RECEIVE}, {@link Type#CLAIM} or {@link Type#CUSTOMS}.
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
    LOAD(true),
    UNLOAD(true),
    RECEIVE(false),
    CLAIM(false),
    CUSTOMS(false);

    private boolean carrierMovementRequired;

    private Type(boolean carrierMovementRequired) {
      this.carrierMovementRequired = carrierMovementRequired;
    }

    /**
     * @return True if a carrier movement association is required for this event type.
     */
    public boolean requiresCarrierMovement() {
      return carrierMovementRequired;
    }

    /**
     * @return True if a carrier movement association is prohibited for this event type.
     */
    public boolean prohibitsCarrierMovement() {
      return !requiresCarrierMovement();
    }
  }


  /**
   * Constructor for events that do not have a carrier movement associated.
   *
   * @param cargo            cargo
   * @param completionTime   completion time
   * @param registrationTime registration time
   * @param type             type of event. Legal values are CLAIM, RECIEVE and CUSTOMS
   * @param location         where the event took place
   */
  public HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type, Location location) {
    this.registrationTime = registrationTime;
    this.completionTime = completionTime;
    this.type = type;
    this.cargo = cargo;
    this.location = location;

    validateType();
  }

    /**
   * Constructor for events that have a carrier movement associated. The location where
   * the event took place is derived from the carrier movement: if the type of event is LOAD,
   * the location is the starting point of the movement, if the type is UNLOAD the location
   * is the end point.
   *
   * @param cargo            cargo
   * @param completionTime   completion time
   * @param registrationTime registration time
   * @param type             type of event. Legal values are LOAD and UNLOAD
   * @param location         where the event took place
   * @param carrierMovement  carrier movement.
   */
  public HandlingEvent(Cargo cargo, Date completionTime, Date registrationTime, Type type, Location location, CarrierMovement carrierMovement) {
    this.registrationTime = registrationTime;
    this.completionTime = completionTime;
    this.type = type;
    this.cargo = cargo;
    this.location = location;
    this.carrierMovement = carrierMovement;


    validateType();
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

  /**
   * Validate that the event type is compatible with the carrier movement value.
   * <p/>
   * Only certain types of events may be associated with a carrier movement.
   */
  private void validateType() {
    if (type.requiresCarrierMovement() && carrierMovement == null) {
      throw new IllegalArgumentException("Carrier movement is required for event type " + type);
    }
    if (type.prohibitsCarrierMovement() && carrierMovement != null) {
      throw new IllegalArgumentException("Carrier movement is not allowed with event type " + type);
    }
  }

  // Needed by Hibernate
  HandlingEvent() {
  }

}
