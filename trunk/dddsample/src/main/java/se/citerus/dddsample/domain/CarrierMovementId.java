package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identifies a particular carrier movement, such as a flight number.
 *
 */
@Embeddable
public class CarrierMovementId {

  @Column(name = "carrier_movement_id")
  private String id;

  public CarrierMovementId(String id) {
    this.id = id;
  }

  /**
   * @return String representation of this carrier movement id.
   */
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

  // Needed by hibernate
  CarrierMovementId() {}

}
