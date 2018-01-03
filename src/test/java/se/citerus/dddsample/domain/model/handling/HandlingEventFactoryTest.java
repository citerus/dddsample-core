package se.citerus.dddsample.domain.model.handling;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import static se.citerus.dddsample.domain.model.location.SampleLocations.TOKYO;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

public class HandlingEventFactoryTest {

  private HandlingEventFactory factory;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private LocationRepository locationRepository;
  private TrackingId trackingId;
  private Cargo cargo;

  @Before
  public void setUp() {

    cargoRepository = mock(CargoRepository.class);
    voyageRepository = new VoyageRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    factory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);

    trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(TOKYO, HELSINKI, new Date());
    cargo = new Cargo(trackingId, routeSpecification);
  }

  @Test
  public void testCreateHandlingEventWithCarrierMovement() throws Exception {
    when(cargoRepository.find(trackingId)).thenReturn(cargo);

    VoyageNumber voyageNumber = CM001.voyageNumber();
    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(), new Date(100), trackingId, voyageNumber, unLocode, Type.LOAD
    );

    assertThat(handlingEvent).isNotNull();
    assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
    assertThat(handlingEvent.voyage()).isEqualTo(CM001);
    assertThat(handlingEvent.cargo()).isEqualTo(cargo);
    assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
    assertThat(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1))).isTrue();
  }

  @Test
  public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
    when(cargoRepository.find(trackingId)).thenReturn(cargo);

    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(), new Date(100), trackingId, null, unLocode, Type.CLAIM
    );

    assertThat(handlingEvent).isNotNull();
    assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
    assertThat(handlingEvent.voyage()).isEqualTo(Voyage.NONE);
    assertThat(handlingEvent.cargo()).isEqualTo(cargo);
    assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
    assertThat(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1))).isTrue();
  }

  @Test
  public void testCreateHandlingEventUnknownLocation() throws Exception {
    when(cargoRepository.find(trackingId)).thenReturn(cargo);

    UnLocode invalid = new UnLocode("NOEXT");
    try {
      factory.createHandlingEvent(
        new Date(), new Date(100), trackingId, CM001.voyageNumber(), invalid, Type.LOAD
      );
      fail("Expected UnknownLocationException");
    } catch (UnknownLocationException expected) {}
  }

  @Test
  public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
    when(cargoRepository.find(trackingId)).thenReturn(cargo);

    try {
      VoyageNumber invalid = new VoyageNumber("XXX");
      factory.createHandlingEvent(
        new Date(), new Date(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownVoyageException");
    } catch (UnknownVoyageException expected) {}
  }

  @Test
  public void testCreateHandlingEventUnknownTrackingId() throws Exception {
    when(cargoRepository.find(trackingId)).thenReturn(null);

    try {
      factory.createHandlingEvent(
        new Date(), new Date(100), trackingId, CM001.voyageNumber(), STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownCargoException");
    } catch (UnknownCargoException expected) {}
  }

}
