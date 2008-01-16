package se.citerus.dddsample.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class CarrierMovement {

  @EmbeddedId
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
