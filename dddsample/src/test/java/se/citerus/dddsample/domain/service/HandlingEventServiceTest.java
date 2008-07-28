package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private DomainEventNotifier domainEventNotifier;
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;

  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), HAMBURG, TOKYO);

  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), HONGKONG, HELSINKI);

  private final CarrierMovement cmAAA_BBB = new CarrierMovement(
          new CarrierMovementId("CAR_001"), CHICAGO, STOCKHOLM);
  
  protected void setUp() throws Exception{
    service = new HandlingEventServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    domainEventNotifier = createMock(DomainEventNotifier.class);

    service.setCargoRepository(cargoRepository);
    service.setCarrierMovementRepository(carrierMovementRepository);
    service.setHandlingEventRepository(handlingEventRepository);
    service.setLocationRepository(locationRepository);
    service.setEventService(domainEventNotifier);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, carrierMovementRepository, handlingEventRepository, domainEventNotifier);
  }

  public void testRegisterEvent() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    final CarrierMovementId carrierMovementId = new CarrierMovementId("AAA_BBB");
    expect(carrierMovementRepository.find(carrierMovementId)).andReturn(cmAAA_BBB);

    final UnLocode unLocode = new UnLocode("SESTO");
    expect(locationRepository.find(unLocode)).andReturn(STOCKHOLM);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));
    domainEventNotifier.cargoWasHandled(isA(HandlingEvent.class));

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    service.register(date, trackingId, carrierMovementId, unLocode, HandlingEvent.Type.LOAD);
  }

  public void testRegisterEventWithoutCarrierMovement() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    handlingEventRepository.save(isA(HandlingEvent.class));
    domainEventNotifier.cargoWasHandled(isA(HandlingEvent.class));

    expect(locationRepository.find(STOCKHOLM.unLocode())).andReturn(STOCKHOLM);

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, domainEventNotifier);

    service.register(date, trackingId, null, STOCKHOLM.unLocode(), HandlingEvent.Type.CLAIM);
  }
  

  public void testRegisterEventInvalidCarrier() throws Exception {
    final Date date = new Date();

    final CarrierMovementId carrierMovementId = new CarrierMovementId("AAA_BBB");
    expect(carrierMovementRepository.find(carrierMovementId)).andReturn(null);

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(new Cargo(trackingId, CHICAGO, STOCKHOLM));

    expect(locationRepository.find(MELBOURNE.unLocode())).andReturn(MELBOURNE);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    try {
      service.register(date, trackingId, carrierMovementId, MELBOURNE.unLocode(), HandlingEvent.Type.UNLOAD);
      fail("Should not be able to register an event with non-existing carrier movement");
    } catch (UnknownCarrierMovementIdException expected) {}
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(null);

    expect(locationRepository.find(HONGKONG.unLocode())).andReturn(HONGKONG);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    try {
      service.register(date, trackingId, null, HONGKONG.unLocode(), HandlingEvent.Type.CLAIM);
      fail("Should not be able to register an event with non-existing cargo");
    } catch (UnknownTrackingIdException expected) {}
  }
  
  public void testRegisterEventInvalidLocation() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(cargoXYZ);
    UnLocode wayOff = new UnLocode("XXYYY");
    expect(locationRepository.find(wayOff)).andReturn(null);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, domainEventNotifier);
    
    try {
      service.register(date, trackingId, null, wayOff, HandlingEvent.Type.CLAIM);
      fail("Should not be able to register an event with non-existing Location");
    } catch (UnknownLocationException expected) {}
  }
}
