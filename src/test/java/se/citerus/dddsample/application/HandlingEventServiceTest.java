package se.citerus.dddsample.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.CM001;

public class HandlingEventServiceTest {
  private HandlingEventServiceImpl service;
  private ApplicationEvents applicationEvents;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;

  private final Cargo cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(HAMBURG, TOKYO, Instant.now()));

  @BeforeEach
  public void setUp() {
    cargoRepository = mock(CargoRepository.class);
    voyageRepository = mock(VoyageRepository.class);
    handlingEventRepository = mock(HandlingEventRepository.class);
    locationRepository = mock(LocationRepository.class);
    applicationEvents = mock(ApplicationEvents.class);

    HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    service = new HandlingEventServiceImpl(handlingEventRepository, applicationEvents, handlingEventFactory);
  }

  @Test
  public void testRegisterEvent() throws Exception {
    when(cargoRepository.find(cargo.trackingId())).thenReturn(cargo);
    when(voyageRepository.find(CM001.voyageNumber())).thenReturn(CM001);
    when(locationRepository.find(STOCKHOLM.unLocode())).thenReturn(STOCKHOLM);

    service.registerHandlingEvent(Instant.now(), cargo.trackingId(), CM001.voyageNumber(), STOCKHOLM.unLocode(), HandlingEvent.Type.LOAD);
    verify(handlingEventRepository, times(1)).store(isA(HandlingEvent.class));
    verify(applicationEvents, times(1)).cargoWasHandled(isA(HandlingEvent.class));
  }
}
