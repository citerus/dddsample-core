package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CargoTest extends TestCase {

  public void testCurrentLocationUnknownWhenNoEvents() throws Exception {
    Location destination = new Location("AUMEL");
    Location origin = new Location("SESTO");
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, destination);

//    assertEquals(Location.UNKNOWN, cargo.currentLocation());
  }
  
  public void testCurrentLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

//    assertEquals(new Location("SESTO"), cargo.currentLocation());
  }

  public void testCurrentLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

//    assertEquals(new Location("AUMEL"), cargo.currentLocation());
  }
  
  public void testCurrentLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

//    assertEquals(new Location("CNHGK"), cargo.currentLocation());
  }

  public void testCurrentLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

//    assertEquals(new Location("DEHAM"), cargo.currentLocation());
  }

  public void testAtFinalLocation() throws Exception {
    Cargo cargo = populateCargoOffMelbourne();

//    assertTrue(cargo.atFinalDestiation());
  }

  public void testNotAtFinalLocationWhenNotUnloaded() throws Exception {
    Cargo cargo = populateCargoOnHongKong();

//    assertFalse(cargo.atFinalDestiation());
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

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, null));

    return cargo;
  }
  
  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, null));
    
    return cargo;
  }
  
  // TODO: Generate test data some better way
  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholmToHamburg));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburgToHongKong));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholmToHamburg));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholmToHamburg));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburgToHongKong));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, hongKongToMelbourne));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, hongKongToMelbourne));

    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholmToHamburg));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburgToHongKong));
//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

//    cargo.handle(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, hongKongToMelbourne));

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
