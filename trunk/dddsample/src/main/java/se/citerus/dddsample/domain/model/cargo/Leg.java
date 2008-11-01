package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.location.Location;

/**
 * An itinerary consists of one or more legs.
 */
public final class Leg implements ValueObject<Leg> {

  private CarrierMovement carrierMovement;
  private Location from;
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

  @Override
  public boolean sameValueAs(final Leg other) {
    return other != null && new EqualsBuilder().
      append(this.carrierMovement, other.carrierMovement).
      append(this.from, other.from).
      append(this.to, other.to).
      isEquals();
  }

  @Override
  public Leg copy() {
    return new Leg(carrierMovement, from, to);
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

  Leg() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  private Long id;

}
