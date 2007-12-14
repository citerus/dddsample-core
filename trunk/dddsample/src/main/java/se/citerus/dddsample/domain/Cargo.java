package se.citerus.dddsample.domain;

public class Cargo {
  private final String trackingId;
  private final Location from;
  private final Location to;

  public Cargo(String trackingId, Location origin, Location finalDestination) {
    this.trackingId = trackingId;
    this.from = origin;
    this.to = finalDestination;
  }

  public DeliveryHistory deliveryHistory() {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }

  public void handle(HandlingEvent event) {
    //To change body of created methods use File | Settings | File Templates.
  }

  public boolean atFinalDestiation() {
    return false;  //To change body of created methods use File | Settings | File Templates.
  }

  public Location currentLocation() {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }
}
