package se.citerus.dddsample.domain.model;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;

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

  @Override
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  public boolean sameValueAs(CarrierMovementId other) {
    return other != null && this.id.equals(other.id);
  }

  // Needed by hibernate
  CarrierMovementId() {
  }
  
}
