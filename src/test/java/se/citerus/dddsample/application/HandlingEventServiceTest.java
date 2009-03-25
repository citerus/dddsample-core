package se.citerus.dddsample.application;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private ApplicationEvents applicationEvents;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;

  private final Cargo cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(HAMBURG, TOKYO, new Date()));

  protected void setUp() throws Exception{
    cargoRepository = createMock(CargoRepository.class);
    voyageRepository = createMock(VoyageRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    applicationEvents = createMock(ApplicationEvents.class);

    HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    service = new HandlingEventServiceImpl(handlingEventRepository, applicationEvents, handlingEventFactory);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, voyageRepository, handlingEventRepository, applicationEvents);
  }

  public void testRegisterEvent() throws Exception {
    expect(cargoRepository.find(cargo.trackingId())).andReturn(cargo);
    expect(voyageRepository.find(CM001.voyageNumber())).andReturn(CM001);
    expect(locationRepository.find(STOCKHOLM.unLocode())).andReturn(STOCKHOLM);
    handlingEventRepository.store(isA(HandlingEvent.class));
    applicationEvents.cargoWasHandled(isA(HandlingEvent.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, applicationEvents);

    service.registerHandlingEvent(new Date(), cargo.trackingId(), CM001.voyageNumber(), STOCKHOLM.unLocode(), HandlingEvent.Type.LOAD);
  }

}
