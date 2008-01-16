package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Identifies a particular carrier (vehicle).
 */
@Embeddable
public class CarrierMovementId implements Serializable {

  private String id;

  public CarrierMovementId(String id) {
    this.id = id;
  }

  public String idString() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

}
