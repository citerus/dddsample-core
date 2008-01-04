package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

/**
 * HandlingEvent links the type of handling with a CarrierMovement.
 * 
 * Since HandlingEvents can be added in any order to a Cargo (or
 * DeliveryHistory), they need to implement Comparable to be able to be sorted
 * in correct order.
 * 
 */
public class HandlingEvent implements Comparable<HandlingEvent> {

  private final Type type;
  private final CarrierMovement carrierMovement;
  private final Date time;

  public enum Type {
    LOAD, UNLOAD, RECEIVE, CLAIM
  }

  public HandlingEvent(Date time, Type type, CarrierMovement carrierMovement) {
    this.time = time;
    this.type = type;
    this.carrierMovement = carrierMovement;
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
   * <li> RECEIVE events: Location.NULL is returned. This basically means that the cargo is at its origin but not yet loaded on a CarrierMovment
   * <li> CLAIM events: Location.NULL is returned. This means that the cargo is at its final destination and has been unloaded and claimed by the customer.
   * <li> LOAD events: The from Location is returned.
   * <li> UNLOAD events: The to Location is returned.
   * </ul> 
   * 
   * @return The Location
   */
  public Location getLocation() {
    Location location = Location.NULL;
    
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
  
  
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public int compareTo(HandlingEvent o) {
    return time.compareTo(o.getTime());
  }
}
