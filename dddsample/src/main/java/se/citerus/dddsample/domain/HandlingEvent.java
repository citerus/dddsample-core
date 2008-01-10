package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

  @Id
  private Long id;

  @Enumerated
  private Type type;

  @ManyToOne
  private CarrierMovement carrierMovement;
  
  private Date time;

  @Transient /*TODO: Change to many-to-many if we decide on that approach*/
  private Set<Cargo> cargos;
  
  public enum Type {
    LOAD, UNLOAD, RECEIVE, CLAIM
  }

  // Exclude the id field from equals() and hashcode()
  private static final String[] excludedFields = {"id"};

  public HandlingEvent(Date time, Type type) {
    this(time, type, null);
  }

  public HandlingEvent(Date time, Type type, CarrierMovement carrierMovement) {
    this.time = time;
    this.type = type;
    this.carrierMovement = carrierMovement;
    this.cargos = new HashSet<Cargo>();
  }

  public Type getType() {
    return type;
  }

  public CarrierMovement getCarrierMovement() {
    return carrierMovement;
  }

  public Date getTime() {
    return time;
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
  public Location getLocation() {
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
  
  public Set<Cargo> getRegisterdCargos(){
    return cargos;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj, excludedFields);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, excludedFields);
  }

  public static Type parseType(String type) {
    return Type.valueOf(type);
  }

  // Needed by Hibernate
  HandlingEvent() {}

}
