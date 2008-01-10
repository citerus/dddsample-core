package se.citerus.dddsample.domain;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;


@Entity
public class CarrierMovement {

  @EmbeddedId
  private CarrierId carrierId;

  @ManyToOne
  private Location from;

  @ManyToOne
  private Location to;

  public CarrierMovement(CarrierId carrierId, Location from, Location to) {
    this.carrierId = carrierId;
    this.from = from;
    this.to = to;
  }

  public CarrierId carrierId() {
    return carrierId;
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
