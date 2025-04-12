package se.citerus.dddsample.domain.model.cargo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;

public class ItineraryTest {
  private final CarrierMovement abc = new CarrierMovement(SHANGHAI, ROTTERDAM, Instant.now(), Instant.now());
  private final CarrierMovement def = new CarrierMovement(ROTTERDAM, GOTHENBURG, Instant.now(), Instant.now());
  private final CarrierMovement ghi = new CarrierMovement(ROTTERDAM, NEWYORK, Instant.now(), Instant.now());
  private final CarrierMovement jkl = new CarrierMovement(SHANGHAI, HELSINKI, Instant.now(), Instant.now());

  Voyage voyage, wrongVoyage;

  @BeforeEach
  public void setUp() {
    voyage = new Voyage.Builder(new VoyageNumber("0123"), SHANGHAI).
      addMovement(ROTTERDAM, Instant.now(), Instant.now()).
      addMovement(GOTHENBURG, Instant.now(), Instant.now()).
      build();

    wrongVoyage = new Voyage.Builder(new VoyageNumber("666"), NEWYORK).
      addMovement(STOCKHOLM, Instant.now(), Instant.now()).
      addMovement(HELSINKI, Instant.now(), Instant.now()).
      build();
  }

  @Test
  public void testCargoOnTrack() {

    TrackingId trackingId = new TrackingId("CARGO1");
    RouteSpecification routeSpecification = new RouteSpecification(SHANGHAI, GOTHENBURG, Instant.now());
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    Itinerary itinerary = new Itinerary(
      List.of(
        new Leg(voyage, SHANGHAI, ROTTERDAM, Instant.now(), Instant.now()),
        new Leg(voyage, ROTTERDAM, GOTHENBURG, Instant.now(), Instant.now())
      )
    );

    //Happy path
    HandlingEvent event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.RECEIVE, SHANGHAI);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.LOAD, ROTTERDAM, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.UNLOAD, GOTHENBURG, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CLAIM, GOTHENBURG);
    assertThat(itinerary.isExpected(event)).isTrue();

    //Customs event changes nothing
    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CUSTOMS, GOTHENBURG);
    assertThat(itinerary.isExpected(event)).isTrue();

    //Received at the wrong location
    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.RECEIVE, HANGZHOU);
    assertThat(itinerary.isExpected(event)).isFalse();

    //Loaded to onto the wrong ship, correct location
    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.LOAD, ROTTERDAM, wrongVoyage);
    assertThat(itinerary.isExpected(event)).isFalse();

    //Unloaded from the wrong ship in the wrong location
    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.UNLOAD, HELSINKI, wrongVoyage);
    assertThat(itinerary.isExpected(event)).isFalse();

    event = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CLAIM, ROTTERDAM);
    assertThat(itinerary.isExpected(event)).isFalse();

  }
  @Test
  public void testNextExpectedEvent() {

  }
  @Test
  public void shouldNotAllowItineraryWithEmptyListOfLegs() {
    assertThatThrownBy(() -> new Itinerary(List.of())).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  public void shouldNotAllowItineraryWithNullListOfLegs() {
    assertThatThrownBy(() -> new Itinerary(null)).isInstanceOf(NullPointerException.class);
  }

}