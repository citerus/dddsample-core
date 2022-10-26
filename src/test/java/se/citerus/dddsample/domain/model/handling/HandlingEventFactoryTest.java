package se.citerus.dddsample.domain.model.handling;

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

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

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
    when(cargoRepository.exists(trackingId)).thenReturn(true);

    VoyageNumber voyageNumber = CM001.voyageNumber();
    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(), new Date(100), trackingId, voyageNumber, unLocode, Type.LOAD
    );

    assertThat(handlingEvent).isNotNull();
    assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
    assertThat(handlingEvent.voyage()).isEqualTo(CM001.voyageNumber());
    assertThat(handlingEvent.cargo()).isEqualTo(cargo.trackingId());
    assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
    assertThat(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1))).isTrue();
  }

  @Test
  public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
    when(cargoRepository.exists(trackingId)).thenReturn(true);

    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(), new Date(100), trackingId, null, unLocode, Type.CLAIM
    );

    assertThat(handlingEvent).isNotNull();
    assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
    assertThat(handlingEvent.voyage()).isEqualTo(Voyage.NONE.voyageNumber());
    assertThat(handlingEvent.cargo()).isEqualTo(cargo.trackingId());
    assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
    assertThat(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1))).isTrue();
  }

  @Test
  public void testCreateHandlingEventUnknownLocation() throws Exception {
    when(cargoRepository.exists(trackingId)).thenReturn(true);

    UnLocode invalid = new UnLocode("NOEXT");
    assertThatThrownBy(() -> factory.createHandlingEvent(
            new Date(), new Date(100), trackingId, CM001.voyageNumber(), invalid, Type.LOAD
    )).isInstanceOf(UnknownLocationException.class).hasMessage("No location with UN locode NOEXT exists in the system");
  }

  @Test
  public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
    when(cargoRepository.exists(trackingId)).thenReturn(true);

    VoyageNumber invalid = new VoyageNumber("XXX");
    assertThatThrownBy(() -> factory.createHandlingEvent(
            new Date(), new Date(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD
    )).isInstanceOf(UnknownVoyageException.class).hasMessage("No voyage with number XXX exists in the system");
  }

  @Test
  public void testCreateHandlingEventUnknownTrackingId() throws Exception {
    when(cargoRepository.exists(trackingId)).thenReturn(false);

    assertThatThrownBy(() -> factory.createHandlingEvent(
            new Date(), new Date(100), trackingId, CM001.voyageNumber(), STOCKHOLM.unLocode(), Type.LOAD
    )).isInstanceOf(UnknownCargoException.class).hasMessage("No cargo with tracking id ABC exists in the system");
  }

}
