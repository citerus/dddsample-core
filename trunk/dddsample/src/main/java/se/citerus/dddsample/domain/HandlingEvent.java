package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;

import javax.persistence.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * HandlingEvent links the type of handling with a CarrierMovement.
 * 
 * Since HandlingEvents can be added in any order to a Cargo (or
 * DeliveryHistory), they need to implement Comparable to be able to be sorted
 * in correct order.
 *
 * TODO: build hierarchy of event types
 */
@Entity
public class HandlingEvent {

  @Id
  private UUID id;

  @Enumerated
  private Type type;

  @ManyToOne
  private CarrierMovement carrierMovement;
  
  private Date timeOccurred;

  private Date timeRegistered;
  
  @ManyToOne
  private Location location;

  @Transient // TODO: cargo-event relation should not be bidirectional
  private Set<Cargo> cargos;
  
  public enum Type {
    LOAD, UNLOAD, RECEIVE, CLAIM
  }

  public HandlingEvent(Date timeOccurred, Date timeRegistered, Type type, Location location) {
    this(timeOccurred, timeRegistered, type, location, null);
  }

  public HandlingEvent(Date timeOccurred, Date timeRegistered, Type type, Location location, CarrierMovement carrierMovement) {
    this.id = UUID.randomUUID();
    this.timeRegistered = timeRegistered;
    this.timeOccurred = timeOccurred;
    this.type = type;
    this.carrierMovement = carrierMovement;
    this.cargos = new HashSet<Cargo>();
    this.location = location;
  }

  /**
   * This determines if two events recieved by the system in fact represent
   * the same real-world event, which may have been reported more than once due to
   * human error, for example.
   *
   * @param other handling event to compare with
   * @return True if these handling events represent the same real-world event.
   */
  public boolean sameAs(HandlingEvent other) {
    return new EqualsBuilder().
            append(this.timeOccurred(), other.timeOccurred()).
            append(this.location(), other.location()).
            append(this.type(), other.type()).
            append(this.carrierMovement(), other.carrierMovement())
            .isEquals();
  }

  public Type type() {
    return type;
  }

  public CarrierMovement carrierMovement() {
    return carrierMovement;
  }

  public Date timeOccurred() {
    return timeOccurred;
  }

  public Date timeRegistered() {
    return timeRegistered;
  }

  /**
   * Returns the Location of the HandlingEvent
   * 
   * @return The Location
   */
  public Location location() {    
    return location;
  }


  /**
   * Add a Cargo to this HandlingEvent
   * 
   * @param cargo
   */
  public void add(Cargo cargo) {
    this.cargos.add(cargo);
  }
  
  /**
   * Returns a Set of Cargos associated with this HandlingEvent
   * 
   * @return The associated Cargos
   */
  public Set<Cargo> cargos(){
    return cargos;
  }


  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof HandlingEvent)) {
      return false;
    }
    HandlingEvent other = (HandlingEvent) obj;
    return new EqualsBuilder().append(this.id, other.id).isEquals();
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  public static Type parseType(String type) {
    return Type.valueOf(type);
  }

  // Needed by Hibernate
  HandlingEvent() {}

}
