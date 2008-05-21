package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.Validate;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identifies a particular carrier movement, such as a flight number.
 */
@Embeddable
public final class CarrierMovementId {

  @Column(name = "carrier_movement_id")
  private String id;

  /**
   * Constructor.
   *
   * @param id Id string.
   */
  public CarrierMovementId(final String id) {
    Validate.notNull(id);
    this.id = id;
  }

  /**
   * @return String representation of this carrier movement id.
   */
  public String idString() {
    return id;
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  // Needed by hibernate
  CarrierMovementId() {
  }

}
