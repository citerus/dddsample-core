package se.citerus.dddsample.domain;

import java.util.Collection;

/**
 * For easy testdata creation.
 * 
 */
public class CargoTestHelper {

  public static Cargo createCargoWithDeliveryHistory(
    TrackingId trackingId, Location origin, Location destination,
    Collection<HandlingEvent> events) {

    final Cargo cargo = new Cargo(trackingId, origin, destination);
    setDeliveryHistory(cargo, events);

    return cargo;
  }

  public static void setDeliveryHistory(Cargo cargo, Collection<HandlingEvent> events) {
    cargo.setDeliveryHistory(new DeliveryHistory(events));
  }
  
}
