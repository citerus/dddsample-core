package se.citerus.dddsample.domain.model.carrier;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.Entity;
import se.citerus.dddsample.domain.model.location.Location;

import java.util.Date;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
public final class CarrierMovement implements Entity<CarrierMovement> {

  private CarrierMovementId carrierMovementId;
  private Location from;
  private Location to;
  private Date departureTime;
  private Date arrivalTime;

  // Null object pattern 
  public static final CarrierMovement NONE = new CarrierMovement(
    new CarrierMovementId("NONE"), Location.UNKNOWN, Location.UNKNOWN,
    new Date(0), new Date(0));

  /**
   * Constructor.
   *
   * @param carrierMovementId carrier movement id
   * @param from from location
   * @param to to location
   * @param departureTime time of departure
   * @param arrivalTime time of arrival
   */
  public CarrierMovement(final CarrierMovementId carrierMovementId,
                         final Location from,
                         final Location to,
                         final Date departureTime,
                         final Date arrivalTime) {
    Validate.noNullElements(new Object[]{carrierMovementId, from, to, departureTime, arrivalTime});
    this.departureTime = departureTime;
    this.arrivalTime = arrivalTime;
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

  public Date departureTime() {
    return departureTime;
  }

  public Date arrivalTime() {
    return arrivalTime;
  }

  public boolean sameIdentityAs(final CarrierMovement other) {
    return carrierMovementId.sameValueAs(other.carrierMovementId);
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
