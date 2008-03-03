package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.HandlingEvent.Type;
import static se.citerus.dddsample.domain.HandlingEvent.Type.*;

import static java.util.Arrays.asList;
import java.util.Date;

public class HandlingEventTest extends TestCase {
  private final Location origin = new Location(new UnLocode("FR","OMX"), "From");
  private final Location finalDestination = new Location(new UnLocode("TO","YYY"), "To");
  private final Location a5 = new Location(new UnLocode("AA","AAA"), "AAAAA");
  private final Location b5 = new Location(new UnLocode("BB","BBB"), "BBBBB");

  public void testNewWithCarrierMovement() throws Exception {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, finalDestination);
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("C01"), origin, finalDestination);

    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), LOAD, origin, carrierMovement);
    assertEquals(origin, e1.location());

    HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, finalDestination, carrierMovement);
    assertEquals(finalDestination, e2.location());

      // These event types prohibit a carrier movement association
    for (Type type : asList(CLAIM, RECEIVE, CUSTOMS)) {
      try {
        new HandlingEvent(cargo, new Date(), new Date(), type, origin, carrierMovement);
        fail("Handling event type " + type + " prohibits carrier movement");
      } catch (IllegalArgumentException expected) {}
    }

      // These event types requires a carrier movement association
    for (Type type : asList(LOAD, UNLOAD)) {
        try {
          new HandlingEvent(cargo, new Date(), new Date(), type, origin);
            fail("Handling event type " + type + " requires carrier movement");
        } catch (IllegalArgumentException expected) {}
    }
  }

  public void testNewWithLocation() throws Exception {
    Location origin = this.origin;
    Location finalDestination = this.finalDestination;
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, finalDestination);

    Location location = new Location(new UnLocode("FO","OOO"), "Foo");
    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), Type.CLAIM, location);
    assertEquals(location, e1.location());
  }

  public void testCurrentLocationLoadEvent() throws Exception {
    CarrierMovementId carrierMovementId = new CarrierMovementId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierMovementId, a5, b5);
    
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), LOAD, a5, cm);
    
    assertEquals(a5, ev.location());
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    CarrierMovementId carrierMovementId = new CarrierMovementId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierMovementId, a5, b5);
    
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), UNLOAD, b5, cm);
    
    assertEquals(b5, ev.location());
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), RECEIVE, a5);

    assertEquals(a5, ev.location());
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), CLAIM, a5);

    assertEquals(a5, ev.location());
  }
  
  public void testParseType() throws Exception {
    assertEquals(CLAIM, valueOf("CLAIM"));
    assertEquals(LOAD, valueOf("LOAD"));
    assertEquals(UNLOAD, valueOf("UNLOAD"));
    assertEquals(RECEIVE, valueOf("RECEIVE"));
  }
  
  public void testParseTypeIllegal() throws Exception {
    try {
      valueOf("NOT_A_HANDLING_EVENT_TYPE");
      assertTrue("Expected IllegaArgumentException to be thrown", false);
    } catch (IllegalArgumentException e) {
      // All's well
    }
  }
  
  public void testEqualsAndSameAs() throws Exception {
    Date timeOccured = new Date();
    Date timeRegistered = new Date();
    CarrierMovementId carrierMovementId = new CarrierMovementId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierMovementId, a5, b5);

    HandlingEvent ev1 = new HandlingEvent(null, timeOccured, timeRegistered, LOAD, a5, cm);
    HandlingEvent ev2 = new HandlingEvent(null, timeOccured, timeRegistered, LOAD, a5, cm);

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertTrue(ev1.equals(ev2));
    assertTrue(ev2.equals(ev1));

    assertTrue(ev1.equals(ev1));

    assertFalse(ev2.equals(null));
    assertFalse(ev2.equals(new Object()));
  }

}
