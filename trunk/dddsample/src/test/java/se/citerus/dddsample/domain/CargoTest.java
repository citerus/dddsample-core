package se.citerus.dddsample.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

public class CargoTest extends TestCase {

  public void testCurrentLocationUnknownWhenNoEvents() throws Exception {
    Location destination = new Location("AUMEL");
    Location origin = new Location("SESTO");
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, destination);

    assertEquals(Location.UNKNOWN, cargo.currentLocation());
  }
  
  public void testCurrentLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertEquals(new Location("SESTO"), cargo.currentLocation());
  }

  public void testCurrentLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertEquals(new Location("AUMEL"), cargo.currentLocation());
  }
  
  public void testCurrentLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertEquals(new Location("CNHGK"), cargo.currentLocation());
  }

  public void testCurrentLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertEquals(new Location("DEHAM"), cargo.currentLocation());
  }

  public void testAtFinalLocation() throws Exception {
    Cargo cargo = populateCargoOffMelbourne();

    assertTrue(cargo.atFinalDestiation());
  }

  public void testNotAtFinalLocationWhenNotUnloaded() throws Exception {
    Cargo cargo = populateCargoOnHongKong();

    assertFalse(cargo.atFinalDestiation());
  }
  
  public void testLastEvent() throws Exception {
    Cargo cargo = populateCargoOutOfOrder();
    
    HandlingEvent lastEvent = cargo.lastEvent();
    
    assertEquals("SESTO", lastEvent.location().unlocode());
    assertEquals(HandlingEvent.Type.LOAD, lastEvent.type());
    assertEquals(getDate("2007-12-11"), lastEvent.timeOccurred());
  }
  
  public void testLastEventWithNoEvents() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));
    
    HandlingEvent lastEvent = cargo.lastEvent();
    assertNull(lastEvent);
  }

  public void testEventsOrderedByTime() throws Exception {
    Cargo cargo = populateCargoOutOfOrder();
    
    List<HandlingEvent> events = cargo.eventsOrderedByTime();
    
    Date lastTime = new Date(0);
    for (HandlingEvent event : events) {
      Date time = event.timeOccurred();
      assertTrue(time.compareTo(lastTime) > 0);
      lastTime = time;
    }
  }
  
  public void testEquality() throws Exception {
    Cargo c1 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("C"));
    Cargo c2 = new Cargo(new TrackingId("CBA"), new Location("A"), new Location("C"));
    Cargo c3 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("X"));
    Cargo c4 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("C"));

    assertTrue("TrackingID, origin and finalDestnation should be equal if Cargos are considered equal", c1.equals(c4));
    assertFalse("Cargos are not equal when TrackingID differ", c1.equals(c2));
    assertFalse("Cargos are not equal when Locations differ", c2.equals(c3));
  }

  
  
  
  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, new Location("SESTO")));

    return cargo;
  }
  
  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    cargo.handle(new HandlingEvent(getDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, new Location("AUMEL")));
    
    return cargo;
  }
  
  // TODO: Generate test data some better way
  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

    cargo.handle(new HandlingEvent(getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, new Location("CNHGK"), hongKongToMelbourne));
    cargo.handle(new HandlingEvent(getDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, new Location("AUMEL"), hongKongToMelbourne));

    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

    cargo.handle(new HandlingEvent(getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, new Location("CNHGK"), hongKongToMelbourne));

    return cargo;
  }

  private Cargo populateCargoOutOfOrder() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-11"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

    cargo.handle(new HandlingEvent(getDate("2001-12-05"), new Date(), HandlingEvent.Type.LOAD, new Location("CNHGK"), hongKongToMelbourne));

    return cargo;
  }
  /**
   * Parse an ISO 8601 (YYYY-MM-DD) String to Date
   *
   * @param isoFormat String to parse.
   * @return Created date instance.
   * @throws ParseException Thrown if parsing fails.
   */
  private Date getDate(String isoFormat) throws ParseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.parse(isoFormat);
  }
}
