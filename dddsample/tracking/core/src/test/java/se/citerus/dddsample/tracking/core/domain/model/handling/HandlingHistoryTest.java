package se.citerus.dddsample.tracking.core.domain.model.handling;

import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

import java.util.Date;

import static java.util.Arrays.asList;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;


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
      addMovement(SHANGHAI, new Date(1), new Date(2)).
      addMovement(DALLAS, new Date(3), new Date(4)).
      build();
    event1 = new HandlingEvent(cargo, toDate("2009-03-05"), toDate("2009-03-05"), HandlingActivityType.LOAD, SHANGHAI, voyage, new OperatorCode("ABCDE"));
    event1duplicate = new HandlingEvent(cargo, toDate("2009-03-05"), toDate("2009-03-07"), HandlingActivityType.LOAD, SHANGHAI, voyage, new OperatorCode("ABCDE"));
    event2 = new HandlingEvent(cargo, toDate("2009-03-10"), toDate("2009-03-06"), HandlingActivityType.UNLOAD, DALLAS, voyage, new OperatorCode("ABCDE"));
    eventOfCargo2 = new HandlingEvent(cargo2, toDate("2009-03-11"), toDate("2009-03-08"), HandlingActivityType.LOAD, GOTHENBURG, voyage, new OperatorCode("ABCDE"));
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
    HandlingEvent event3Customs = new HandlingEvent(cargo, toDate("2009-03-11"), toDate("2009-03-11"), HandlingActivityType.CUSTOMS, DALLAS);
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
