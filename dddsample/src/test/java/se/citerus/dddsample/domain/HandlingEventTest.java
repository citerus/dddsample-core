package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.HandlingEvent.Type;
import static se.citerus.dddsample.domain.HandlingEvent.Type.*;

import static java.util.Arrays.asList;
import java.util.Date;

public class HandlingEventTest extends TestCase {

  public void testNewWithCarrierMovement() throws Exception {
    Location origin = new Location("FROM");
    Location finalDestination = new Location("TO");
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, finalDestination);
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("C01"), origin, finalDestination);

    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), LOAD, origin, carrierMovement);
    assertEquals(origin, e1.location());

    HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, finalDestination, carrierMovement);
    assertEquals(finalDestination, e2.location());
    
    for (Type type : asList(CLAIM, RECEIVE, CUSTOMS)) {
      try {
        new HandlingEvent(cargo, new Date(), new Date(), type, origin, carrierMovement);
        fail("Handling event with carrier movement and type " + type + " is not legal");
      } catch (IllegalArgumentException expected) {}
    }
  }

  public void testNewWithLocation() throws Exception {
    Location origin = new Location("FROM");
    Location finalDestination = new Location("TO");
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, finalDestination);

    Location location = new Location("FOO");
    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), Type.CLAIM, location);
    assertEquals(location, e1.location());

    
  }

  public void testCurrentLocationLoadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierMovementId carrierMovementId = new CarrierMovementId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierMovementId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), LOAD, locationAAA, cm);
    
    assertEquals(locationAAA, ev.location());
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierMovementId carrierMovementId = new CarrierMovementId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierMovementId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), UNLOAD, locationBBB, cm);
    
    assertEquals(locationBBB, ev.location());
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), RECEIVE, new Location("TEST"));

    assertEquals(new Location("TEST"), ev.location());
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, new Date(), new Date(), CLAIM, new Location("TEST"));

    assertEquals(new Location("TEST"), ev.location());
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
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierMovementId carrierMovementId = new CarrierMovementId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierMovementId, locationAAA, locationBBB);

    HandlingEvent ev1 = new HandlingEvent(null, timeOccured, timeRegistered, LOAD, locationAAA, cm);
    HandlingEvent ev2 = new HandlingEvent(null, timeOccured, timeRegistered, LOAD, locationAAA, cm);

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertTrue(ev1.equals(ev2));
    assertTrue(ev2.equals(ev1));

    assertTrue(ev1.equals(ev1));

    assertFalse(ev2.equals(null));
    assertFalse(ev2.equals(new Object()));
  }

}
