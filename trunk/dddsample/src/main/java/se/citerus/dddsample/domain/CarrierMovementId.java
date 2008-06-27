package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Identifies a particular carrier movement, such as a flight number.
 */
public final class CarrierMovementId implements ValueObject<CarrierMovementId> {

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

  public boolean sameValueAs(CarrierMovementId other) {
    return other != null && this.id.equals(other.id);
  }

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return idString();
  }

  CarrierMovementId() {
    // Needed by hibernate
   }
  
}
