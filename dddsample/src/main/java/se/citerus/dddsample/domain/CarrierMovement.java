package se.citerus.dddsample.domain;

import javax.persistence.*;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
@Entity
public class CarrierMovement {

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  private CarrierMovementId carrierMovementId;

  @ManyToOne
  private Location from;

  @ManyToOne
  private Location to;

  public CarrierMovement(CarrierMovementId carrierMovementId, Location from, Location to) {
    this.carrierMovementId = carrierMovementId;
    this.from = from;
    this.to = to;
  }

  public CarrierMovementId carrierId() {
    return carrierMovementId;
  }

  public Location from() {
    return from;
  }

  public Location to() {
    return to;
  }

  // Needed by Hibernate
  CarrierMovement() {
  }


  /**
   * Entities compare by identity, therefore the carrierMovementId field is the only basis of comparison. For
   * persistence we have an id field, but it is not used for identiy comparison.
   * <p/>
   * Compare this behavior to the value object {@link se.citerus.dddsample.domain.Leg#sameValueAs(Leg)}
   *
   * @param other The other cargo.
   * @return <code>true</code> if the given carrier movement's and this carrier movement's carrierId is the same,
   *         regardles of other attributes.
   */
  public boolean sameIdentityAs(CarrierMovement other) {
    if (carrierMovementId != null ? !carrierMovementId.equals(other.carrierMovementId) : other.carrierMovementId != null)
      return false;

    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CarrierMovement that = (CarrierMovement) o;

    return sameIdentityAs(that);
  }

  @Override
  public int hashCode() {
    int result;
    result = (id != null ? id.hashCode() : 0);
    result = 31 * result + (carrierMovementId != null ? carrierMovementId.hashCode() : 0);
    result = 31 * result + (from != null ? from.hashCode() : 0);
    result = 31 * result + (to != null ? to.hashCode() : 0);
    return result;
  }
}
