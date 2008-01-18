package se.citerus.dddsample.domain;

import javax.persistence.*;


/**
 * A carrier movement is a vessel voyage from one location to another.
 *
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
  CarrierMovement() {}

}
