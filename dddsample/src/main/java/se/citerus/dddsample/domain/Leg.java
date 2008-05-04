package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.Validate;

import javax.persistence.*;

/**
 * An itinerary consists of one or more legs.
 */
@Entity
public class Leg {
  @Id
  @GeneratedValue
  private Long id;

  // TODO: why is this not related to CarrierMovement?
  @Embedded
  private CarrierMovementId carrierMovementId;
  @ManyToOne
  private Location from;
  @ManyToOne
  private Location to;

  public Leg(CarrierMovementId carrierMovementId, Location from, Location to) {
    Validate.noNullElements(new Object[] {carrierMovementId, from, to});
    this.carrierMovementId = carrierMovementId;
    this.from = from;
    this.to = to;
  }

  public Location from() {
    return from;
  }

  public Location to() {
    return to;
  }

  public CarrierMovementId carrierMovementId() {
    return carrierMovementId;
  }

  
  /**
   * Value objects compare by value, therefore the id field which must be part of the class in order to support
   * persistence is ignored in the comparison.
   * <p/>
   * Compare this behavior to the entity {@link se.citerus.dddsample.domain.Cargo#sameIdentityAs(Cargo)}
   *
   * @param other The other leg.
   * @return <code>true</code> if the given leg's and this leg's attributes are the same.
   */
  public boolean sameValueAs(Leg other) {
    return other != null && new EqualsBuilder().
      append(this.carrierMovementId, other.carrierMovementId).
      append(this.from, other.from).
      append(this.to, other.to).
      isEquals();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Leg leg = (Leg) o;

    return sameValueAs(leg);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13,17).
      append(carrierMovementId).
      append(from).
      append(to).
      toHashCode();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  Leg() {
    // Needed by Hibernate
  }
}
