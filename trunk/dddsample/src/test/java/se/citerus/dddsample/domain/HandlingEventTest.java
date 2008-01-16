package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.HandlingEvent.Type;

import java.util.Date;

public class HandlingEventTest extends TestCase {
  public void testCurrentLocation() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    Date timeOccured = new Date();
    Date timeRegistrated = new Date();
    HandlingEvent ev = new HandlingEvent(timeOccured, timeRegistrated, HandlingEvent.Type.LOAD, locationAAA, cm);
    
    assertEquals(locationAAA, ev.location());
  }
  
  public void testCurrentLocationMisdirectedCargo() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    Location locationCCC = new Location("CCC");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(new Date(), new Date(), HandlingEvent.Type.UNLOAD, locationCCC, cm);
    
    assertEquals(locationCCC, ev.location());
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

    HandlingEvent ev1 = new HandlingEvent(timeOccured, timeRegistered, HandlingEvent.Type.LOAD, locationAAA, cm);
    HandlingEvent ev2 = new HandlingEvent(timeOccured, timeRegistered, HandlingEvent.Type.LOAD, locationAAA, cm);

    // They are the same real-world event
    assertTrue(ev1.sameAs(ev2));
    assertTrue(ev2.sameAs(ev1));

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertFalse(ev1.equals(ev2));
    assertFalse(ev2.equals(ev1));

    assertTrue(ev1.equals(ev1));
  }

}
