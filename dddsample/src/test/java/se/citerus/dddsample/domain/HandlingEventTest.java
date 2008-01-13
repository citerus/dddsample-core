package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.HandlingEvent.Type;

import java.util.Date;

public class HandlingEventTest extends TestCase {
  public void testCurrentLocationLoadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(new Date(), new Date(), HandlingEvent.Type.LOAD, cm);
    
    assertEquals(locationAAA, ev.getLocation());
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(new Date(), new Date(), HandlingEvent.Type.UNLOAD, cm);
    
    assertEquals(locationBBB, ev.getLocation());
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(new Date(), new Date(), HandlingEvent.Type.RECEIVE, null);

    assertEquals(Location.UNKNOWN, ev.getLocation());
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(new Date(), new Date(), HandlingEvent.Type.CLAIM, null);

    assertEquals(Location.UNKNOWN, ev.getLocation());
  }
  
  public void testParseType() throws Exception {
    assertEquals(Type.CLAIM, HandlingEvent.parseType("CLAIM"));
    assertEquals(Type.LOAD, HandlingEvent.parseType("LOAD"));
    assertEquals(Type.UNLOAD, HandlingEvent.parseType("UNLOAD"));
    assertEquals(Type.RECEIVE, HandlingEvent.parseType("RECEIVE"));
  }
  
  public void testParseTypeIllegal() throws Exception {
    try {
      HandlingEvent.parseType("NOT_A_HANDLING_EVENT_TYPE");
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
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);

    HandlingEvent ev1 = new HandlingEvent(timeOccured, timeRegistered, HandlingEvent.Type.LOAD, cm);
    HandlingEvent ev2 = new HandlingEvent(timeOccured, timeRegistered, HandlingEvent.Type.LOAD, cm);

    // They are the same real-world event
    assertTrue(ev1.sameAs(ev2));
    assertTrue(ev2.sameAs(ev1));

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertFalse(ev1.equals(ev2));
    assertFalse(ev2.equals(ev1));
  }

}
