package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * An itinerary consists of one or more legs.
 */
@Entity
public final class Leg {
  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  private CarrierMovement carrierMovement;

  @ManyToOne
  private Location from;
  @ManyToOne
  private Location to;

  /**
   * Constructor.
   *
   * @param carrierMovement
   * @param from
   * @param to
   */
  public Leg(final CarrierMovement carrierMovement, final Location from, final Location to) {
    Validate.noNullElements(new Object[]{carrierMovement, from, to});
    this.carrierMovement = carrierMovement;
    this.from = from;
    this.to = to;
  }

  public Location from() {
    return from;
  }

  public Location to() {
    return to;
  }

  public CarrierMovement carrierMovement() {
    return carrierMovement;
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
  public boolean sameValueAs(final Leg other) {
    return other != null && new EqualsBuilder().
      append(this.carrierMovement, other.carrierMovement).
      append(this.from, other.from).
      append(this.to, other.to).
      isEquals();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Leg leg = (Leg) o;

    return sameValueAs(leg);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(carrierMovement).
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
