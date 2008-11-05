package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.Schedule;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CargoTest extends TestCase {

  private Set<HandlingEvent> events;
  private Voyage voyage;

  protected void setUp() throws Exception {
    events = new HashSet<HandlingEvent>();

    voyage = new Voyage(new VoyageNumber("0123"), new Schedule(Arrays.asList(
      new CarrierMovement(STOCKHOLM, HAMBURG, new Date(), new Date()),
      new CarrierMovement(HAMBURG, HONGKONG, new Date(), new Date()),
      new CarrierMovement(HONGKONG, MELBOURNE, new Date(), new Date())
    )));
  }

  public void testlastKnownLocationUnknownWhenNoEvents() throws Exception {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);

    assertEquals(Location.UNKNOWN, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationReceived() throws Exception {
    Cargo cargo = populateCargoReceivedStockholm();

    assertEquals(STOCKHOLM, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationClaimed() throws Exception {
    Cargo cargo = populateCargoClaimedMelbourne();

    assertEquals(MELBOURNE, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationUnloaded() throws Exception {
    Cargo cargo = populateCargoOffHongKong();

    assertEquals(HONGKONG, cargo.lastKnownLocation());
  }

  public void testlastKnownLocationloaded() throws Exception {
    Cargo cargo = populateCargoOnHamburg();

    assertEquals(HAMBURG, cargo.lastKnownLocation());
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
    Cargo c1 = new Cargo(new TrackingId("ABC"), STOCKHOLM, HONGKONG);
    Cargo c2 = new Cargo(new TrackingId("CBA"), STOCKHOLM, HONGKONG);
    Cargo c3 = new Cargo(new TrackingId("ABC"), STOCKHOLM, MELBOURNE);
    Cargo c4 = new Cargo(new TrackingId("ABC"), STOCKHOLM, HONGKONG);

    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c4));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c1.equals(c3));
    assertTrue("Cargos should be equal when TrackingIDs are equal", c3.equals(c4));
    assertFalse("Cargos are not equal when TrackingID differ", c1.equals(c2));
  }

  public void testIsUnloadedAtFinalDestination() throws Exception {
    assertFalse(new Cargo().isUnloadedAtDestination());

    Cargo cargo = setUpCargoWithItinerary(HANGZOU, TOKYO, NEWYORK);
    assertFalse(cargo.isUnloadedAtDestination());

    // Adding an event unrelated to unloading at final destination
    events.add(
      new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU));
    cargo.setDeliveryHistory(new Delivery(events));
    assertFalse(cargo.isUnloadedAtDestination());

    CarrierMovement cm1 = new CarrierMovement(HANGZOU, NEWYORK, new Date(), new Date());
    Voyage voyage = new Voyage(new VoyageNumber("0123"), new Schedule(Arrays.asList(cm1)));

    // Adding an unload event, but not at the final destination
    events.add(
      new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, TOKYO, voyage));
    cargo.setDeliveryHistory(new Delivery(events));
    assertFalse(cargo.isUnloadedAtDestination());

    // Adding an event in the final destination, but not unload
    events.add(
      new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CUSTOMS, NEWYORK));
    cargo.setDeliveryHistory(new Delivery(events));
    assertFalse(cargo.isUnloadedAtDestination());

    // Finally, cargo is unloaded at final destination
    events.add(
      new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, NEWYORK, voyage));
    cargo.setDeliveryHistory(new Delivery(events));
    assertTrue(cargo.isUnloadedAtDestination());
  }

  /* TODO implement nextExpectedEvent
  public void testNextExpectedEvent() {
    Cargo cargo = setUpCargoWithItinerary(HANGZOU, TOKYO, NEWYORK);
    CarrierMovementId cmid = new CarrierMovementId("CM1");
    CarrierMovement cm1 = new CarrierMovement(cmid, HANGZOU, TOKYO);
    CarrierMovement cm2 = new CarrierMovement(cmid, TOKYO, NEWYORK);

    HandlingEvent event1 = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU, null);

    assertEquals(event1, cargo.nextExpectedEvent());

    cargo.deliveryHistory().addEvent(event1);

    HandlingEvent event2 = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, HANGZOU, cm1);

    assertEquals(event2, cargo.nextExpectedEvent());

    cargo.deliveryHistory().addEvent(event2);
  }
  */


  // TODO: Generate test data some better way
  private Cargo populateCargoReceivedStockholm() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);

    HandlingEvent he = new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.RECEIVE, STOCKHOLM);
    events.add(he);
    cargo.setDeliveryHistory(new Delivery(events));

    return cargo;
  }

  private Cargo populateCargoClaimedMelbourne() throws Exception {
    final Cargo cargo = populateCargoOffMelbourne();

    events.add(new HandlingEvent(cargo, getDate("2007-12-09"), new Date(), HandlingEvent.Type.CLAIM, MELBOURNE));
    cargo.setDeliveryHistory(new Delivery(events));

    return cargo;
  }

  private Cargo populateCargoOffHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);


    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    cargo.setDeliveryHistory(new Delivery(events));
    return cargo;
  }

  private Cargo populateCargoOnHamburg() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));

    cargo.setDeliveryHistory(new Delivery(events));
    return cargo;
  }

  private Cargo populateCargoOffMelbourne() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, HONGKONG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-07"), new Date(), HandlingEvent.Type.UNLOAD, MELBOURNE, voyage));

    cargo.setDeliveryHistory(new Delivery(events));
    return cargo;
  }

  private Cargo populateCargoOnHongKong() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);

    events.add(new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, voyage));
    events.add(new HandlingEvent(cargo, getDate("2007-12-04"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, voyage));

    events.add(new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.LOAD, HONGKONG, voyage));

    cargo.setDeliveryHistory(new Delivery(events));
    return cargo;
  }

  public void testIsMisdirected() throws Exception {
    //A cargo with no itinerary is not misdirected
    Cargo cargo = new Cargo(new TrackingId("TRKID"), SHANGHAI, GOTHENBURG);
    assertFalse(cargo.isMisdirected());

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);

    //A cargo with no handling events is not misdirected
    assertFalse(cargo.isMisdirected());

    Collection<HandlingEvent> handlingEvents = new ArrayList<HandlingEvent>();

    /*
    CarrierMovement abc = new CarrierMovement(new CarrierMovementId("ABC"), SHANGHAI, ROTTERDAM, new Date(), new Date());
    CarrierMovement def = new CarrierMovement(new CarrierMovementId("DEF"), ROTTERDAM, GOTHENBURG, new Date(), new Date());
    CarrierMovement ghi = new CarrierMovement(new CarrierMovementId("GHI"), ROTTERDAM, NEWYORK, new Date(), new Date());
    */
    
    //Happy path
    handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80), HandlingEvent.Type.LOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(90), new Date(100), HandlingEvent.Type.UNLOAD, GOTHENBURG, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(110), new Date(120), HandlingEvent.Type.CLAIM, GOTHENBURG));
    handlingEvents.add(new HandlingEvent(cargo, new Date(130), new Date(140), HandlingEvent.Type.CUSTOMS, GOTHENBURG));

    events.addAll(handlingEvents);
    cargo.setDeliveryHistory(new Delivery(events));
    assertFalse(cargo.isMisdirected());

    //Try a couple of failing ones

    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU));
    events.addAll(handlingEvents);
    cargo.setDeliveryHistory(new Delivery(events));

    assertTrue(cargo.isMisdirected());


    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(70), new Date(80), HandlingEvent.Type.LOAD, ROTTERDAM, voyage));

    events.addAll(handlingEvents);
    cargo.setDeliveryHistory(new Delivery(events));

    assertTrue(cargo.isMisdirected());


    cargo = setUpCargoWithItinerary(SHANGHAI, ROTTERDAM, GOTHENBURG);
    handlingEvents = new ArrayList<HandlingEvent>();

    handlingEvents.add(new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.RECEIVE, SHANGHAI));
    handlingEvents.add(new HandlingEvent(cargo, new Date(30), new Date(40), HandlingEvent.Type.LOAD, SHANGHAI, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(50), new Date(60), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage));
    handlingEvents.add(new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, ROTTERDAM));

    events.addAll(handlingEvents);
    cargo.setDeliveryHistory(new Delivery(events));

    assertTrue(cargo.isMisdirected());
  }

  private Cargo setUpCargoWithItinerary(Location origin, Location midpoint, Location destination) {
    Cargo cargo = new Cargo(new TrackingId("CARGO1"), origin, destination);

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(voyage, origin, midpoint, new Date(), new Date()),
        new Leg(voyage, midpoint, destination, new Date(), new Date())
      )
    );

    cargo.assignToRoute(itinerary);
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
