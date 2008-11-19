package se.citerus.dddsample.application;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.application.messaging.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.SampleVoyages;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private SystemEvents systemEvents;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;
  private HandlingEventFactory handlingEventFactory;

  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), HAMBURG, TOKYO);

  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), HONGKONG, HELSINKI);

  protected void setUp() throws Exception{
    cargoRepository = createMock(CargoRepository.class);
    voyageRepository = createMock(VoyageRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    systemEvents = createMock(SystemEvents.class);
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    service = new HandlingEventServiceImpl(handlingEventRepository, systemEvents, handlingEventFactory);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, voyageRepository, handlingEventRepository, systemEvents);
  }

  public void testRegisterEvent() throws Exception {
    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    final VoyageNumber voyageNumber = new VoyageNumber("AAA_BBB");
    expect(voyageRepository.find(voyageNumber)).andReturn(SampleVoyages.CM001);

    final UnLocode unLocode = new UnLocode("SESTO");
    expect(locationRepository.find(unLocode)).andReturn(STOCKHOLM);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));
    systemEvents.cargoWasHandled(isA(HandlingEvent.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, systemEvents);

    service.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, voyageNumber, HandlingEvent.Type.LOAD, unLocode
    ));
  }

  public void testRegisterEventWithoutCarrierMovement() throws Exception {
    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    handlingEventRepository.save(isA(HandlingEvent.class));
    systemEvents.cargoWasHandled(isA(HandlingEvent.class));

    expect(locationRepository.find(STOCKHOLM.unLocode())).andReturn(STOCKHOLM);

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, systemEvents);

    service.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, null, HandlingEvent.Type.RECEIVE, STOCKHOLM.unLocode()
    ));
  }
  

  public void testRegisterEventInvalidCarrier() throws Exception {
    final VoyageNumber voyageNumber = new VoyageNumber("AAA_BBB");
    expect(voyageRepository.find(voyageNumber)).andReturn(null);

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(new Cargo(trackingId, CHICAGO, STOCKHOLM));

    expect(locationRepository.find(MELBOURNE.unLocode())).andReturn(MELBOURNE);

    systemEvents.rejectHandlingEventRegistrationAttempt(isA(HandlingEventRegistrationAttempt.class), isA(CannotCreateHandlingEventException.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, systemEvents);
    
    service.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, voyageNumber, HandlingEvent.Type.UNLOAD, MELBOURNE.unLocode()
    ));
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(null);

    expect(locationRepository.find(HONGKONG.unLocode())).andReturn(HONGKONG);

    systemEvents.rejectHandlingEventRegistrationAttempt(isA(HandlingEventRegistrationAttempt.class), isA(CannotCreateHandlingEventException.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, systemEvents);
    
      service.register(new HandlingEventRegistrationAttempt(
        new Date(), new Date(), trackingId, new VoyageNumber("V001"), HandlingEvent.Type.CLAIM, HONGKONG.unLocode()
      ));
  }
  
  public void testRegisterEventInvalidLocation() throws Exception {
    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(cargoXYZ);
    UnLocode wayOff = new UnLocode("XXYYY");
    expect(locationRepository.find(wayOff)).andReturn(null);

    systemEvents.rejectHandlingEventRegistrationAttempt(isA(HandlingEventRegistrationAttempt.class), isA(CannotCreateHandlingEventException.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, systemEvents);
    
    service.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, null, HandlingEvent.Type.CLAIM, wayOff
    ));
  }
}
