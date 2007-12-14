package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.util.SortedSet;

public class TrackingScenarioTest extends TestCase {

  public void testTrackingScenarioStage1() {

    Cargo cargo = populateCargo();

    DeliveryHistory deliveryHistory = cargo.deliveryHistory();

    SortedSet<HandlingEvent> handlingEvents = deliveryHistory.events();

    assertEquals(4, handlingEvents.size());
    final HandlingEvent event = handlingEvents.last();
    assertSame(HandlingEvent.Type.OFF, event.type());
    assertFalse(cargo.atFinalDestiation());
    assertEquals("CNHKG", cargo.currentLocation().unlocode());

  }

  private Cargo populateCargo() {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg =
       new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(HandlingEvent.Type.ON, stockholmToHamburg));
    cargo.handle(new HandlingEvent(HandlingEvent.Type.OFF, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong =
       new CarrierMovement(new Location("DEHAM"), new Location("CNHGK"));

    cargo.handle(new HandlingEvent(HandlingEvent.Type.ON, hamburgToHongKong));
    cargo.handle(new HandlingEvent(HandlingEvent.Type.OFF, hamburgToHongKong));

    return cargo;
  }

}
