package se.citerus.dddsample.application;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.SampleVoyages;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.handling.*;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.DomainEventNotifier;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private DomainEventNotifier domainEventNotifier;
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
    domainEventNotifier = createMock(DomainEventNotifier.class);
    service = new HandlingEventServiceImpl(handlingEventRepository, domainEventNotifier);
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, voyageRepository, handlingEventRepository, domainEventNotifier);
  }

  public void testRegisterEvent() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    final VoyageNumber voyageNumber = new VoyageNumber("AAA_BBB");
    expect(voyageRepository.find(voyageNumber)).andReturn(SampleVoyages.CM001);

    final UnLocode unLocode = new UnLocode("SESTO");
    expect(locationRepository.find(unLocode)).andReturn(STOCKHOLM);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));
    domainEventNotifier.cargoWasHandled(isA(HandlingEvent.class));

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, domainEventNotifier);

    final HandlingEvent event = handlingEventFactory.createHandlingEvent(date, trackingId, voyageNumber, unLocode, HandlingEvent.Type.LOAD);
    service.register(event);
  }

  public void testRegisterEventWithoutCarrierMovement() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    handlingEventRepository.save(isA(HandlingEvent.class));
    domainEventNotifier.cargoWasHandled(isA(HandlingEvent.class));

    expect(locationRepository.find(STOCKHOLM.unLocode())).andReturn(STOCKHOLM);

    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, domainEventNotifier);

    final HandlingEvent event = handlingEventFactory.createHandlingEvent(date, trackingId, null, STOCKHOLM.unLocode(), HandlingEvent.Type.CLAIM);
    service.register(event);
  }
  

  public void testRegisterEventInvalidCarrier() throws Exception {
    final Date date = new Date();

    final VoyageNumber voyageNumber = new VoyageNumber("AAA_BBB");
    expect(voyageRepository.find(voyageNumber)).andReturn(null);

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(new Cargo(trackingId, CHICAGO, STOCKHOLM));

    expect(locationRepository.find(MELBOURNE.unLocode())).andReturn(MELBOURNE);
    
    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    try {
      final HandlingEvent event = handlingEventFactory.createHandlingEvent(date, trackingId, voyageNumber, MELBOURNE.unLocode(), HandlingEvent.Type.UNLOAD);
      service.register(event);
      fail("Should not be able to register an event with non-existing carrier movement");
    } catch (UnknownVoyageException expected) {}
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(null);

    expect(locationRepository.find(HONGKONG.unLocode())).andReturn(HONGKONG);
    
    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    try {
      final HandlingEvent event = handlingEventFactory.createHandlingEvent(date, trackingId, null, HONGKONG.unLocode(), HandlingEvent.Type.CLAIM);
      service.register(event);
      fail("Should not be able to register an event with non-existing cargo");
    } catch (UnknownCargoException expected) {}
  }
  
  public void testRegisterEventInvalidLocation() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(cargoXYZ);
    UnLocode wayOff = new UnLocode("XXYYY");
    expect(locationRepository.find(wayOff)).andReturn(null);
    
    replay(cargoRepository, voyageRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    try {
      final HandlingEvent event = handlingEventFactory.createHandlingEvent(date, trackingId, null, wayOff, HandlingEvent.Type.CLAIM);
      service.register(event);
      fail("Should not be able to register an event with non-existing Location");
    } catch (UnknownLocationException expected) {}
  }
}
