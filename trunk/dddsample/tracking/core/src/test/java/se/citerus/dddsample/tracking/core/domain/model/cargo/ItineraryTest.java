package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItineraryTest extends TestCase {

  Voyage voyage, wrongVoyage, pacific, transcontinental;

  protected void setUp() throws Exception {

    pacific = new Voyage.Builder(new VoyageNumber("4567"), SHANGHAI).
      addMovement(LONGBEACH, new Date(), new Date()).
      build();

    transcontinental = new Voyage.Builder(new VoyageNumber("4567"), LONGBEACH).
      addMovement(CHICAGO, new Date(), new Date()).
      addMovement(NEWYORK, new Date(), new Date()).
      build();

    voyage = new Voyage.Builder(new VoyageNumber("0123"), SHANGHAI).
      addMovement(ROTTERDAM, new Date(), new Date()).
      addMovement(GOTHENBURG, new Date(), new Date()).
      build();

    wrongVoyage = new Voyage.Builder(new VoyageNumber("666"), NEWYORK).
      addMovement(STOCKHOLM, new Date(), new Date()).
      addMovement(HELSINKI, new Date(), new Date()).
      build();
  }

  public void testIfCargoIsOnTrack() {

    Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(voyage, SHANGHAI, ROTTERDAM, new Date(), new Date()),
        new Leg(voyage, ROTTERDAM, GOTHENBURG, new Date(), new Date())
      )
    );

    // HandlingActivity.Load(cargo, RECEIVE, SHANGHAI, toDate("2009-05-03"))
    //Happy path
    HandlingActivity receiveShanghai = new HandlingActivity(RECEIVE, SHANGHAI);
    assertTrue(itinerary.isExpected(receiveShanghai));

    HandlingActivity loadShanghai = new HandlingActivity(LOAD, SHANGHAI, voyage);
    assertTrue(itinerary.isExpected(loadShanghai));

    HandlingActivity unloadRotterdam = new HandlingActivity(UNLOAD, ROTTERDAM, voyage);
    assertTrue(itinerary.isExpected(unloadRotterdam));

    HandlingActivity loadRotterdam = new HandlingActivity(LOAD, ROTTERDAM, voyage);
    assertTrue(itinerary.isExpected(loadRotterdam));

    HandlingActivity unloadGothenburg = new HandlingActivity(UNLOAD, GOTHENBURG, voyage);
    assertTrue(itinerary.isExpected(unloadGothenburg));

    HandlingActivity claimGothenburg = new HandlingActivity(CLAIM, GOTHENBURG);
    assertTrue(itinerary.isExpected(claimGothenburg));

    //TODO Customs event can only be interpreted properly by knowing the destination of the cargo.
    // This can be inferred from the Itinerary, but it isn't definitive. So, do we answer based on
    // the end of the itinerary (even though this would probably not be used in the app) or do we
    // ignore this at itinerary level somehow and leave it purely as a cargo responsibility.
    // (See customsClearancePoint tests in CargoTest)
//    HandlingActivity customsGothenburg = new HandlingActivity(CUSTOMS, GOTHENBURG);
//    assertTrue(itinerary.isExpected(customsGothenburg));

    //Received at the wrong location
    HandlingActivity receiveHangzou = new HandlingActivity(RECEIVE, HANGZOU);
    assertFalse(itinerary.isExpected(receiveHangzou));

    //Loaded to onto the wrong ship, correct location
    HandlingActivity loadRotterdam666 = new HandlingActivity(LOAD, ROTTERDAM, wrongVoyage);
    assertFalse(itinerary.isExpected(loadRotterdam666));

    //Unloaded from the wrong ship in the wrong location
    HandlingActivity unloadHelsinki = new HandlingActivity(UNLOAD, HELSINKI, wrongVoyage);
    assertFalse(itinerary.isExpected(unloadHelsinki));

    HandlingActivity claimRotterdam = new HandlingActivity(CLAIM, ROTTERDAM);
    assertFalse(itinerary.isExpected(claimRotterdam));
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
