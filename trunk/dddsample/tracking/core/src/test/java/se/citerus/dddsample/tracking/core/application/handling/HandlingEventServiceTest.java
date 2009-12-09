package se.citerus.dddsample.tracking.core.application.handling;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.tracking.core.application.event.SystemEvents;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.handling.OperatorCode;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;

import se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private SystemEvents systemEvents;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;

  private final Cargo cargo = new Cargo(new TrackingId("ABC"), new RouteSpecification(HAMBURG, TOKYO, new Date()));

  protected void setUp() throws Exception {
    cargoRepository = createMock(CargoRepository.class);
    voyageRepository = createMock(VoyageRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    systemEvents = createMock(SystemEvents.class);

    HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    service = new HandlingEventServiceImpl(handlingEventRepository, systemEvents, handlingEventFactory);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, voyageRepository, handlingEventRepository, systemEvents);
  }

  public void testRegisterEvent() throws Exception {
    expect(cargoRepository.find(cargo.trackingId())).andReturn(cargo);
    expect(voyageRepository.find(SampleVoyages.pacific1.voyageNumber())).andReturn(SampleVoyages.pacific1);
    expect(locationRepository.find(STOCKHOLM.unLocode())).andReturn(STOCKHOLM);
    handlingEventRepository.store(isA(HandlingEvent.class));
    systemEvents.notifyOfHandlingEvent(isA(HandlingEvent.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, systemEvents);

    service.registerHandlingEvent(new Date(), cargo.trackingId(), SampleVoyages.pacific1.voyageNumber(), STOCKHOLM.unLocode(), HandlingEvent.Type.LOAD, new OperatorCode("ABCDE"));
  }

}
