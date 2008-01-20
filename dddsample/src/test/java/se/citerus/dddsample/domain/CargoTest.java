package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CargoTest extends TestCase {

  // TODO:
  // it seems that events are not added to the system by cargo.deliveryHistory().addEvent(),
  // but rather new HandlingEvents are stored and associated with its cargo. This test should
  // work against the repositories or the service layer. The delivery history of a cargo should be
  // read-only from the cargo end. // PeBa

  public void testlastKnownLocationUnknownWhenNoEvents() throws Exception {
    Location destination = new Location("AUMEL");
    Location origin = new Location("SESTO");
    Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, destination);

    assertEquals(Location.UNKNOWN, cargo.lastKnownLocation());
  }
  
  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertEquals(new Location("SESTO"), cargo.lastKnownLocation());
  }

  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertEquals(new Location("AUMEL"), cargo.lastKnownLocation());
  }
  
  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertEquals(new Location("CNHGK"), cargo.lastKnownLocation());
  }

  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertEquals(new Location("DEHAM"), cargo.lastKnownLocation());
  }

  public void testAtFinalLocation() throws Exception {
    Cargo cargo = populateCargoOffMelbourne();

    assertTrue(cargo.hasArrived());
  }

  public void testNotAtFinalLocationWhenNotUnloaded() throws Exception {
    Cargo cargo = populateCargoOnHongKong();

    assertFalse(cargo.hasArrived());
  }
  
  public void testEquality() throws Exception {
    Cargo c1 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("C"));
    Cargo c2 = new Cargo(new TrackingId("CBA"), new Location("A"), new Location("C"));
    Cargo c3 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("X"));
    Cargo c4 = new Cargo(new TrackingId("ABC"), new Location("A"), new Location("C"));

    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c4));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c3));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c3.equals(c4));
    assertFalse("Cargos are not equal when TrackingID differ", c1.equals(c2));
  }

  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, new Location("SESTO")));

    return cargo;
  }
  
  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, new Location("AUMEL")));
    
    return cargo;
  }
  
  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, new Location("CNHGK"), hongKongToMelbourne));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, new Location("AUMEL"), hongKongToMelbourne));

    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("DEHAM"), new Location("CNHGK"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHGK"), hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("CNHGK"), new Location("AUMEL"));

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, new Location("CNHGK"), hongKongToMelbourne));

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
