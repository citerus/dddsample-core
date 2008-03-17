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
}
