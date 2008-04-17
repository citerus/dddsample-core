package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ItineraryTest extends TestCase {
  private final Location shanghai = new Location(new UnLocode("CN", "SHA"), "Shanghai");
  private final Location rotterdam = new Location(new UnLocode("NL", "RTM"), "Rotterdam");
  private final Location goteborg = new Location(new UnLocode("SE", "GOT"), "Goteborg");
  private final Location hangzhou = new Location(new UnLocode("CN", "HGH"), "Hangzhou");
  private final Location nyc = new Location(new UnLocode("US", "NYC"), "New York");
  private final Location longBeach = new Location(new UnLocode("US", "LGB"), "Long Beach");
  private final CarrierMovement abc = new CarrierMovement(new CarrierMovementId("ABC"), shanghai, rotterdam);
  private final CarrierMovement def = new CarrierMovement(new CarrierMovementId("DEF"), rotterdam, goteborg);
  private final CarrierMovement ghi = new CarrierMovement(new CarrierMovementId("GHI"), rotterdam, nyc);
  private final CarrierMovement jkl = new CarrierMovement(new CarrierMovementId("JKL"), shanghai, longBeach);

  public void testCargoOnTrack() throws Exception {

    Cargo cargo = new Cargo(new TrackingId("CARGO1")); //Immutable things go into the constructor

    //Mutable things in setters
    cargo.setOrigin(shanghai);
    cargo.setDestination(goteborg);


    Itinerary itinerary = new Itinerary(
       new Leg(new CarrierMovementId("ABC"), shanghai, rotterdam),
       new Leg(new CarrierMovementId("DEF"), rotterdam, goteborg)
    );

    //Happy path
    HandlingEvent event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, shanghai,null);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, shanghai, abc);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, rotterdam, abc);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, rotterdam, def);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, goteborg, def);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, goteborg, null);
    assertTrue(itinerary.isExpected(event));

    //Customs event changes nothing
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CUSTOMS, goteborg, null);
    assertTrue(itinerary.isExpected(event));

    //Received at the wrong location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, hangzhou, null);
    assertFalse(itinerary.isExpected(event));

    //Loaded to onto the wrong ship, correct location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, rotterdam, ghi);
    assertFalse(itinerary.isExpected(event));

    //Unloaded from the wrong ship in the wrong location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, longBeach, jkl);
    assertFalse(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, rotterdam, null);
    assertFalse(itinerary.isExpected(event));

  }

  public void testNextExpectedEvent() throws Exception {

  }

  public void testCreateItinerary() throws Exception {
    //An empty legs list is not OK:
    try {
      new Itinerary();
      fail("An empty itinerary is not OK");
    } catch (IllegalArgumentException iae) {
      //Expected
    }

    try {
      new Itinerary(new ArrayList<Leg>());
      fail("An empty itinerary is not OK");
    } catch (IllegalArgumentException iae) {
      //Expected
    }

    try {
      List<Leg> legs = null;
      new Itinerary(legs);
      fail("Null itinerary is not OK");
    } catch (IllegalArgumentException iae) {
      //Expected
    }
  }

}