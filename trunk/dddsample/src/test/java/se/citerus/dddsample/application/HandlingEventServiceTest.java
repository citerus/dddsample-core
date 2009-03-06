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
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private ApplicationEvents applicationEvents;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;
  private HandlingEventFactory handlingEventFactory;

  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new RouteSpecification(HAMBURG, TOKYO, new Date()));
  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), new RouteSpecification(HONGKONG, HELSINKI, new Date()));

  protected void setUp() throws Exception{
    cargoRepository = createMock(CargoRepository.class);
    voyageRepository = createMock(VoyageRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    applicationEvents = createMock(ApplicationEvents.class);
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    service = new HandlingEventServiceImpl(handlingEventRepository, applicationEvents, handlingEventFactory);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, voyageRepository, handlingEventRepository, applicationEvents);
  }

  public void testRegisterEvent() throws Exception {
    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    final VoyageNumber voyageNumber = new VoyageNumber("AAA_BBB");
    expect(voyageRepository.find(voyageNumber)).andReturn(SampleVoyages.CM001);

    final UnLocode unLocode = new UnLocode("SESTO");
    expect(locationRepository.find(unLocode)).andReturn(STOCKHOLM);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.store(isA(HandlingEvent.class));
    applicationEvents.cargoWasHandled(isA(HandlingEvent.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, applicationEvents);

    final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, voyageNumber, HandlingEvent.Type.LOAD, unLocode
    );
    service.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
  }

  public void testRegisterEventWithoutCarrierMovement() throws Exception {
    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    handlingEventRepository.store(isA(HandlingEvent.class));
    applicationEvents.cargoWasHandled(isA(HandlingEvent.class));

    expect(locationRepository.find(STOCKHOLM.unLocode())).andReturn(STOCKHOLM);

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, applicationEvents);

    final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, null, HandlingEvent.Type.RECEIVE, STOCKHOLM.unLocode()
    );
    service.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
  }
  

  public void testRegisterEventInvalidCarrier() throws Exception {
    final VoyageNumber voyageNumber = new VoyageNumber("AAA_BBB");
    expect(voyageRepository.find(voyageNumber)).andReturn(null);

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(new Cargo(trackingId, new RouteSpecification(CHICAGO, STOCKHOLM, new Date())));

    expect(locationRepository.find(MELBOURNE.unLocode())).andReturn(MELBOURNE);

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, applicationEvents);

    final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, voyageNumber, HandlingEvent.Type.UNLOAD, MELBOURNE.unLocode()
    );
    service.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(null);

    expect(locationRepository.find(HONGKONG.unLocode())).andReturn(HONGKONG);

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, applicationEvents);

    final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, new VoyageNumber("V001"), HandlingEvent.Type.CLAIM, HONGKONG.unLocode()
    );
    service.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
  }
  
  public void testRegisterEventInvalidLocation() throws Exception {
    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(cargoXYZ);
    UnLocode wayOff = new UnLocode("XXYYY");
    expect(locationRepository.find(wayOff)).andReturn(null);

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, applicationEvents);

    final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, null, HandlingEvent.Type.CLAIM, wayOff
    );
    service.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
  }
}
