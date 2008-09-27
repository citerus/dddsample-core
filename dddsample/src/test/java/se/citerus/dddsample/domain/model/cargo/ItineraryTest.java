package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItineraryTest extends TestCase {
  private final CarrierMovement abc = new CarrierMovement(new CarrierMovementId("ABC"), SHANGHAI, ROTTERDAM, new Date(), new Date());
  private final CarrierMovement def = new CarrierMovement(new CarrierMovementId("DEF"), ROTTERDAM, GOTHENBURG, new Date(), new Date());
  private final CarrierMovement ghi = new CarrierMovement(new CarrierMovementId("GHI"), ROTTERDAM, NEWYORK, new Date(), new Date());
  private final CarrierMovement jkl = new CarrierMovement(new CarrierMovementId("JKL"), SHANGHAI, HELSINKI, new Date(), new Date());

  public void testCargoOnTrack() throws Exception {

    Cargo cargo = new Cargo(new TrackingId("CARGO1"), SHANGHAI, GOTHENBURG);

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(new CarrierMovement(new CarrierMovementId("ABC"), SHANGHAI, ROTTERDAM, new Date(), new Date()), SHANGHAI, ROTTERDAM),
        new Leg(new CarrierMovement(new CarrierMovementId("DEF"), ROTTERDAM, GOTHENBURG, new Date(), new Date()), ROTTERDAM, GOTHENBURG)
      )
    );

    //Happy path
    HandlingEvent event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, SHANGHAI);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, SHANGHAI, abc);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, ROTTERDAM, abc);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, ROTTERDAM, def);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, GOTHENBURG, def);
    assertTrue(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, GOTHENBURG);
    assertTrue(itinerary.isExpected(event));

    //Customs event changes nothing
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CUSTOMS, GOTHENBURG);
    assertTrue(itinerary.isExpected(event));

    //Received at the wrong location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZOU);
    assertFalse(itinerary.isExpected(event));

    //Loaded to onto the wrong ship, correct location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, ROTTERDAM, ghi);
    assertFalse(itinerary.isExpected(event));

    //Unloaded from the wrong ship in the wrong location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, HELSINKI, jkl);
    assertFalse(itinerary.isExpected(event));

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, ROTTERDAM);
    assertFalse(itinerary.isExpected(event));

  }

  public void testNextExpectedEvent() throws Exception {

  }

  public void testCreateItinerary() throws Exception {
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