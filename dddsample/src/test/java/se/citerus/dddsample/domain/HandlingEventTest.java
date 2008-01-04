package se.citerus.dddsample.domain;

import junit.framework.TestCase;

public class HandlingEventTest extends TestCase {
  public void testCurrentLocationLoadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierMovement cm = new CarrierMovement(locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.LOAD, cm);
    
    assertEquals(locationAAA, ev.getLocation());
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierMovement cm = new CarrierMovement(locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.UNLOAD, cm);
    
    assertEquals(locationBBB, ev.getLocation());
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.RECEIVE, null);

    assertEquals(Location.NULL, ev.getLocation());
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.CLAIM, null);

    assertEquals(Location.NULL, ev.getLocation());
  }
}
