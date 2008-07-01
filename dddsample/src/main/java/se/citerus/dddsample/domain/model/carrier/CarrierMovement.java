package se.citerus.dddsample.domain.model.carrier;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.Entity;
import se.citerus.dddsample.domain.model.location.Location;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
public final class CarrierMovement implements Entity<CarrierMovement> {

  private CarrierMovementId carrierMovementId;
  private Location from;
  private Location to;

  /**
   * Constructor.
   *
   * @param carrierMovementId
   * @param from
   * @param to
   */
  public CarrierMovement(final CarrierMovementId carrierMovementId, final Location from, final Location to) {
    Validate.noNullElements(new Object[]{carrierMovementId, from, to});
    this.carrierMovementId = carrierMovementId;
    this.from = from;
    this.to = to;
  }

  public CarrierMovementId carrierMovementId() {
    return carrierMovementId;
  }

  public Location from() {
    return from;
  }

  public Location to() {
    return to;
  }

  public boolean sameIdentityAs(final CarrierMovement other) {
    return carrierMovementId.equals(other.carrierMovementId);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final CarrierMovement that = (CarrierMovement) o;

    return sameIdentityAs(that);
  }

  /**
   * @return Hashcode of carrier movement id.
   */
  @Override
  public int hashCode() {
    return carrierMovementId.hashCode();
  }

  CarrierMovement() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  private Long id;

}
