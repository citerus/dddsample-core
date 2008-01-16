package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


/**
 * A Cargo an entity identifed by TrackingId and is capable of getting its DeliveryHistory plus a number
 * of convenience operation for finding current destination etc.
 */
@Entity
public class Cargo {

  @EmbeddedId
  private TrackingId trackingId;

  @ManyToOne
  private Location origin;
  
  @ManyToOne
  private Location finalDestination;

  public Cargo(TrackingId trackingId, Location origin, Location finalDestination) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
  }

  public TrackingId trackingId() {
    return trackingId;
  }

  public Location origin() {
    return origin;
  }

  public Location finalDestination() {
    return finalDestination;
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Cargo)) {
      return false;
    }
    Cargo rhs = (Cargo) obj;
    return new EqualsBuilder()
      .append(trackingId, rhs.trackingId)
      .append(origin, rhs.origin)
      .append(finalDestination, rhs.finalDestination)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(7, 39)
    .append(trackingId)
    .append(origin)
    .append(finalDestination)
    .toHashCode();
  }
  
  // Needed by Hibernate
  Cargo() {}
  
}
