package se.citerus.dddsample.domain;

public class Leg {
  private CarrierMovementId carrierMovementId;
  private Location from;
  private Location to;

  public Leg(CarrierMovementId carrierMovementId, Location from, Location to) {
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
    if (carrierMovementId != null ? !carrierMovementId.equals(other.carrierMovementId) : other.carrierMovementId != null)
      return false;
    if (from != null ? !from.equals(other.from) : other.from != null) return false;
    if (to != null ? !to.equals(other.to) : other.to != null) return false;

    return true;
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
    int result;
    result = (carrierMovementId != null ? carrierMovementId.hashCode() : 0);
    result = 31 * result + (from != null ? from.hashCode() : 0);
    result = 31 * result + (to != null ? to.hashCode() : 0);
    return result;
  }
}
