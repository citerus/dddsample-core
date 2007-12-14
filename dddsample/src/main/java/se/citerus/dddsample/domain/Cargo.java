package se.citerus.dddsample.domain;

public class Cargo {
  private final TrackingId trackingId;
  private final Location from;
  private final Location to;

  public Cargo(TrackingId trackingId, Location origin, Location finalDestination) {
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

  public TrackingId trackingId() {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }
}
