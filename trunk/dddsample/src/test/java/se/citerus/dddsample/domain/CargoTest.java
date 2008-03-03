package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CargoTest extends TestCase {
  private final Location stockholm = new Location(new UnLocode("SE","STO"), "Stockholm");
  private final Location melbourne = new Location(new UnLocode("AU","MEL"), "Melbourne");
  private final Location hongkong = new Location(new UnLocode("CN","HKG"), "Hongkong");
  private final Location hamburg = new Location(new UnLocode("DE","HAM"), "Hamburg");

  // TODO:
  // it seems that events are not added to the system by cargo.deliveryHistory().addEvent(),
  // but rather new HandlingEvents are stored and associated with its cargo. This test should
  // work against the repositories or the service layer. The delivery history of a cargo should be
  // read-only from the cargo end. // PeBa



  public void testlastKnownLocationUnknownWhenNoEvents() throws Exception {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

    assertEquals(Location.UNKNOWN, cargo.lastKnownLocation());
  }
  
  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertEquals(stockholm, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertEquals(melbourne, cargo.lastKnownLocation());
  }
  
  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertEquals(hongkong, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertEquals(hamburg, cargo.lastKnownLocation());
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
    Cargo c1 = new Cargo(new TrackingId("ABC"), stockholm, hongkong);
    Cargo c2 = new Cargo(new TrackingId("CBA"), stockholm, hongkong);
    Cargo c3 = new Cargo(new TrackingId("ABC"), stockholm, melbourne);
    Cargo c4 = new Cargo(new TrackingId("ABC"), stockholm, hongkong);

    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c4));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c3));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c3.equals(c4));
    assertFalse("Cargos are not equal when TrackingID differ", c1.equals(c2));
  }

  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

    HandlingEvent he = new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, stockholm);
    cargo.deliveryHistory().addEvent(he);

    return cargo;
  }
  
  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, melbourne));
    
    return cargo;
  }
  
  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), stockholm, hamburg);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholm, stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, hamburg, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), hamburg, hongkong);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburg, hamburgToHongKong));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, hongkong, hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), stockholm, hamburg);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholm, stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, hamburg, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), hamburg, hongkong);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburg, hamburgToHongKong));

    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), stockholm, hamburg);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholm, stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, hamburg, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), hamburg, hongkong);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburg, hamburgToHongKong));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, hongkong, hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierMovementId("CAR_001"), hongkong, melbourne);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, hongkong, hongKongToMelbourne));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, melbourne, hongKongToMelbourne));

    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), stockholm, hamburg);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholm, stockholmToHamburg));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, hamburg, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_001"), hamburg, hongkong);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburg, hamburgToHongKong));
    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, hongkong, hamburgToHongKong));

    final CarrierMovement hongKongToMelbourne = new CarrierMovement(
            new CarrierMovementId("CAR_001"), hongkong, melbourne);

    cargo.deliveryHistory().addEvent(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, hongkong, hongKongToMelbourne));

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
