package se.citerus.dddsample.domain.model.handling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.application.util.DateUtils.toDate;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;

public class HandlingHistoryTest {
  Cargo cargo;
  Voyage voyage;
  HandlingEvent event1;
  HandlingEvent event1duplicate;
  HandlingEvent event2;
  HandlingHistory handlingHistory;

  @BeforeEach
  public void setUp() {
    cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(SHANGHAI, DALLAS, toDate("2009-04-01")));
    voyage = new Voyage.Builder(new VoyageNumber("X25"), HONGKONG).
      addMovement(SHANGHAI, new Date(), new Date()).
      addMovement(DALLAS, new Date(), new Date()).
      build();
    event1 = new HandlingEvent(cargo, toDate("2009-03-05"), new Date(100), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    event1duplicate = new HandlingEvent(cargo, toDate("2009-03-05"), new Date(200), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    event2 = new HandlingEvent(cargo, toDate("2009-03-10"), new Date(150), HandlingEvent.Type.UNLOAD, DALLAS, voyage);

    handlingHistory = new HandlingHistory(List.of(event2, event1, event1duplicate));
  }

  @Test
  public void testDistinctEventsByCompletionTime() {
    assertThat(handlingHistory.distinctEventsByCompletionTime()).isEqualTo(List.of(event1, event2));
  }

  @Test
  public void testMostRecentlyCompletedEvent() {
    assertThat(handlingHistory.mostRecentlyCompletedEvent()).isEqualTo(event2);
  }
  
}
