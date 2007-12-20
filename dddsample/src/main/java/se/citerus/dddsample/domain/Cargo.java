package se.citerus.dddsample.domain;


/**
 * A Cargo an entity identifed by TrackingId and is capable of getting its DeliveryHistory plus a number
 * of convenience operation for finding current destination etc.
 */
public class Cargo {
  private final TrackingId trackingId;
  private final Location origin;
  private final Location finalDestination;
  private DeliveryHistory history;

  public Cargo(TrackingId trackingId, Location origin, Location finalDestination) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;

    this.history = new DeliveryHistory();
  }

  public DeliveryHistory deliveryHistory() {
    return history;
  }

  public void handle(HandlingEvent event) {
    history.addEvent(event);
  }

  public boolean atFinalDestiation() {
    return currentLocation().equals(finalDestination);
  }

  public Location currentLocation() {
    HandlingEvent lastEvent = history.last();
    if (lastEvent == null) {
      return origin;
    }
    CarrierMovement cm = lastEvent.getCarrierMovement();

    return (lastEvent.type() == HandlingEvent.Type.LOAD) ? cm.from() : cm.to();
  }

  public TrackingId trackingId() {
    return trackingId;
  }
}
