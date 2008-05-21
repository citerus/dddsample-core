package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;

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
    Validate.noNullElements(new Object[] {carrierMovementId, from, to});
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
   * @return <code>true</code> if the given carrier movement's and this carrier movement's carrier id are the same,
   *         regardles of other attributes.
   */
  public boolean sameIdentityAs(CarrierMovement other) {
    return carrierMovementId.equals(other.carrierMovementId);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CarrierMovement that = (CarrierMovement) o;

    return sameIdentityAs(that);
  }

  /**
   * @return Hashcode of carrier movement id.
   */
  @Override
  public int hashCode() {
    return carrierMovementId.hashCode();
  }
}
