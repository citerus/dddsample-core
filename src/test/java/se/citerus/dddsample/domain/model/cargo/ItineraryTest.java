package se.citerus.dddsample.domain.model.cargo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;

public class ItineraryTest {
  private final CarrierMovement abc = new CarrierMovement(SHANGHAI, ROTTERDAM, new Date(), new Date());
  private final CarrierMovement def = new CarrierMovement(ROTTERDAM, GOTHENBURG, new Date(), new Date());
  private final CarrierMovement ghi = new CarrierMovement(ROTTERDAM, NEWYORK, new Date(), new Date());
  private final CarrierMovement jkl = new CarrierMovement(SHANGHAI, HELSINKI, new Date(), new Date());

  Voyage voyage, wrongVoyage;

  @BeforeEach
  public void setUp() {
    voyage = new Voyage.Builder(new VoyageNumber("0123"), SHANGHAI).
      addMovement(ROTTERDAM, new Date(), new Date()).
      addMovement(GOTHENBURG, new Date(), new Date()).
      build();

    wrongVoyage = new Voyage.Builder(new VoyageNumber("666"), NEWYORK).
      addMovement(STOCKHOLM, new Date(), new Date()).
      addMovement(HELSINKI, new Date(), new Date()).
      build();
  }

  @Test
  public void testCargoOnTrack() {

    TrackingId trackingId = new TrackingId("CARGO1");
    RouteSpecification routeSpecification = new RouteSpecification(SHANGHAI, GOTHENBURG, new Date());
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    Itinerary itinerary = new Itinerary(
      List.of(
        new Leg(voyage, SHANGHAI, ROTTERDAM, new Date(), new Date()),
        new Leg(voyage, ROTTERDAM, GOTHENBURG, new Date(), new Date())
      )
    );

    //Happy path
    HandlingEvent event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, SHANGHAI);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, SHANGHAI, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, ROTTERDAM, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, ROTTERDAM, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, GOTHENBURG, voyage);
    assertThat(itinerary.isExpected(event)).isTrue();

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, GOTHENBURG);
    assertThat(itinerary.isExpected(event)).isTrue();

    //Customs event changes nothing
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CUSTOMS, GOTHENBURG);
    assertThat(itinerary.isExpected(event)).isTrue();

    //Received at the wrong location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.RECEIVE, HANGZHOU);
    assertThat(itinerary.isExpected(event)).isFalse();

    //Loaded to onto the wrong ship, correct location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.LOAD, ROTTERDAM, wrongVoyage);
    assertThat(itinerary.isExpected(event)).isFalse();

    //Unloaded from the wrong ship in the wrong location
    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.UNLOAD, HELSINKI, wrongVoyage);
    assertThat(itinerary.isExpected(event)).isFalse();

    event = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, ROTTERDAM);
    assertThat(itinerary.isExpected(event)).isFalse();

  }
  @Test
  public void testNextExpectedEvent() {

  }
  @Test
  public void testCreateItinerary() {
    try {
      new Itinerary(new ArrayList<>());
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