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
    LOAD, UNLOAD
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
  
  public Location getLocation() {
    return (type == HandlingEvent.Type.LOAD) ? carrierMovement.from() : carrierMovement.to();
  }
  
  
  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public int compareTo(HandlingEvent o) {
    return time.compareTo(o.getTime());
  }
}
