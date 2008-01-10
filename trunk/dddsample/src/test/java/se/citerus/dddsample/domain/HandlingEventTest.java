package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.HandlingEvent.Type;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class HandlingEventTest extends TestCase {
  public void testCurrentLocationLoadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.LOAD, cm);
    
    assertEquals(locationAAA, ev.getLocation());
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.UNLOAD, cm);
    
    assertEquals(locationBBB, ev.getLocation());
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.RECEIVE, null);

    assertEquals(Location.UNKNOWN, ev.getLocation());
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(null, HandlingEvent.Type.CLAIM, null);

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
  
  public void testEquality() throws Exception {
    Date date = Calendar.getInstance().getTime();
    Location locationAAA = new Location("AAA");
    Location locationBBB = new Location("BBB");
    CarrierId carrierId = new CarrierId("CAR_001");
    CarrierMovement cm = new CarrierMovement(carrierId, locationAAA, locationBBB);
    
    Cargo cargo1 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("C"));
    Cargo cargo2 = new Cargo(new TrackingId("CBA"), new Location("C"), new Location("A"));
    Cargo cargo3 = new Cargo(new TrackingId("CBA"), new Location("C"), new Location("A")); //Identical to cargo2
    
    Set<Cargo> cargos1 = new HashSet<Cargo>();
    cargos1.add(cargo1);
    
    Set<Cargo> cargos2 = new HashSet<Cargo>();
    cargos2.add(cargo1);
    cargos2.add(cargo2);
    
    Set<Cargo> cargos3 = new HashSet<Cargo>();
    cargos3.add(cargo1);
    cargos3.add(cargo3);
    
    
    HandlingEvent ev1 = new HandlingEvent(date, HandlingEvent.Type.LOAD, cm);
    ev1.register(cargos1);
    
    HandlingEvent ev2 = new HandlingEvent(date, HandlingEvent.Type.LOAD, cm);
    ev2.register(cargos2);
    
    HandlingEvent ev3 = new HandlingEvent(date, HandlingEvent.Type.LOAD, cm);
    ev3.register(cargos2);  
    
    assertFalse("HandlingEvents should not be considered equal if the Set of Cargo is not equal", ev1.equals(ev2));
    assertTrue("HandlingEvents should be considered equal if the Set of Cargo is equal", ev2.equals(ev3));
  }
}
