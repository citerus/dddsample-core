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

  @Transient // TODO: cargo-event relation should not be bidirectional
  private Set<Cargo> cargos;
  
  public enum Type {
    LOAD, UNLOAD, RECEIVE, CLAIM
  }

  public HandlingEvent(Date timeOccurred, Date timeRegistered, Type type) {
    this(timeOccurred, timeRegistered, type, null);
  }

  public HandlingEvent(Date timeOccurred, Date timeRegistered, Type type, CarrierMovement carrierMovement) {
    this.id = UUID.randomUUID();
    this.timeRegistered = timeRegistered;
    this.timeOccurred = timeOccurred;
    this.type = type;
    this.carrierMovement = carrierMovement;
    this.cargos = new HashSet<Cargo>();
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
   * Returns the Location of the Cargo. The location is calculated based on the following rules:
   * <br>For
   * <ul>
   * <li> RECEIVE events: Location.UNKNOWN is returned. This basically means that the cargo is at its origin but not yet loaded on a CarrierMovment
   * <li> CLAIM events: Location.UNKNOWN is returned. This means that the cargo is at its final destination and has been unloaded and claimed by the customer.
   * <li> LOAD events: The from Location is returned.
   * <li> UNLOAD events: The to Location is returned.
   * </ul> 
   * 
   * @return The Location
   */
  public Location location() {
    Location location = Location.UNKNOWN;
    
    //My gosh! A switch statement....
    switch (type) {
      case LOAD:
        location = carrierMovement.from();
      break;

      case UNLOAD:
        location = carrierMovement.to();
      break;
      
      default: 
        // for others (RECEIVE, CLAIM) Location.NULL is fine...
      break;
    }
    
    return location;
  }


  /**
   * Register a set of Cargos
   * 
   * @param cargosToRegister
   */
  public void register(Set<Cargo> cargosToRegister) {
    this.cargos.addAll(cargosToRegister);
  }
  
  public Set<Cargo> registerdCargos(){
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
