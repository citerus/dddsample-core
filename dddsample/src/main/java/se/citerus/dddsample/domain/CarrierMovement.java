package se.citerus.dddsample.domain;

public class CarrierMovement {
  private final Location from;
  private final Location to;

  public CarrierMovement(Location from, Location to) {
    this.from = from;
    this.to = to;
  }

  public Location from() {
    return from;
  }

  public Location to() {
    return to;
  }

}
