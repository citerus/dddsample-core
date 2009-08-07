package se.citerus.dddsample.domain.model.handling;

import junit.framework.TestCase;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import static java.util.Arrays.asList;
import java.util.Date;


public class HandlingHistoryTest extends TestCase {
  Cargo cargo;
  Cargo cargo2;
  Voyage voyage;
  HandlingEvent event1;
  HandlingEvent event1duplicate;
  HandlingEvent event2;
  HandlingEvent eventOfCargo2;
  HandlingHistory handlingHistory;

  protected void setUp() throws Exception {
    cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(SHANGHAI, DALLAS, toDate("2009-04-01")));
    cargo2 = new Cargo(new TrackingId("DEF"), new RouteSpecification(SHANGHAI, NEWYORK, toDate("2009-04-15")));

    voyage = new Voyage.Builder(new VoyageNumber("X25"), HONGKONG).
      addMovement(SHANGHAI, new Date(), new Date()).
      addMovement(DALLAS, new Date(), new Date()).
      build();
    event1 = new HandlingEvent(cargo, toDate("2009-03-05"), toDate("2009-03-05"), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    event1duplicate = new HandlingEvent(cargo, toDate("2009-03-05"), toDate("2009-03-07"), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    event2 = new HandlingEvent(cargo, toDate("2009-03-10"), toDate("2009-03-06"), HandlingEvent.Type.UNLOAD, DALLAS, voyage);
    eventOfCargo2 = new HandlingEvent(cargo2, toDate("2009-03-11"), toDate("2009-03-08"), HandlingEvent.Type.LOAD, GOTHENBURG, voyage);
  }

  public void testDistinctEventsByCompletionTime() {
    handlingHistory = HandlingHistory.fromEvents(asList(event2, event1, event1duplicate));

    assertEquals(asList(event1, event2), handlingHistory.distinctEventsByCompletionTime());
  }

  public void testMostRecentlyCompletedEvent() {
    handlingHistory = HandlingHistory.fromEvents(asList(event2, event1, event1duplicate));

    assertEquals(event2, handlingHistory.mostRecentlyCompletedEvent());
  }

  public void testMostRecentLoadOrUnload() {
    // TODO
    HandlingEvent event3Customs = new HandlingEvent(cargo, toDate("2009-03-11"), toDate("2009-03-11"), HandlingEvent.Type.CUSTOMS, DALLAS);
    handlingHistory = HandlingHistory.fromEvents(asList(event2, event1, event1duplicate, event3Customs));

    assertEquals(event3Customs, handlingHistory.mostRecentlyCompletedEvent());
    assertEquals(event2, handlingHistory.mostRecentPhysicalHandling());
  }

  public void testUniqueCargoOfEvents() {
    try {
      handlingHistory = HandlingHistory.fromEvents(asList(event1, event2, eventOfCargo2));
      fail("A handling history should only accept handling events for a single unique cargo");
    } catch (IllegalArgumentException expected) {
    }
  }

  public void testCargo() {
    handlingHistory = HandlingHistory.fromEvents(asList(event1, event2));
    assertEquals(cargo, handlingHistory.cargo());

    handlingHistory = HandlingHistory.fromEvents(asList(eventOfCargo2));
    assertEquals(cargo2, handlingHistory.cargo());
  }

}
