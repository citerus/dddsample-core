package se.citerus.dddsample.domain.model.handling;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;


public class HandlingHistoryTest extends TestCase {
  Cargo cargo;
  Voyage voyage;
  HandlingEvent event1;
  HandlingEvent event1duplicate;
  HandlingEvent event2;
  HandlingHistory handlingHistory;

  protected void setUp() throws Exception {
    cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(SHANGHAI, DALLAS, toDate("2009-04-01")));
    voyage = new Voyage.Builder(new VoyageNumber("X25"), HONGKONG).
      addMovement(SHANGHAI, new Date(), new Date()).
      addMovement(DALLAS, new Date(), new Date()).
      build();
    event1 = new HandlingEvent(cargo, toDate("2009-03-05"), new Date(100), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    event1duplicate = new HandlingEvent(cargo, toDate("2009-03-05"), new Date(200), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    event2 = new HandlingEvent(cargo, toDate("2009-03-10"), new Date(150), HandlingEvent.Type.UNLOAD, DALLAS, voyage);

    handlingHistory = new HandlingHistory(asList(event2, event1, event1duplicate));
  }

  public void testDistinctEventsByCompletionTime() {
    assertThat(handlingHistory.distinctEventsByCompletionTime()).isEqualTo(asList(event1, event2));
  }

  public void testMostRecentlyCompletedEvent() {
    assertThat(handlingHistory.mostRecentlyCompletedEvent()).isEqualTo(event2);
  }
  
}
