package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private EventService eventService;
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;

  private Location origin = new Location(new UnLocode("AF","ROM"), "AFROM");
  private Location finalDestination = new Location(new UnLocode("AB","CTO"), "ABCTO");
  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), origin, finalDestination);

  private Location xfrom = new Location(new UnLocode("XF","ROM"), "XFROM");
  private Location xyzto = new Location(new UnLocode("XY","ZTO"), "XYZTO");
  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), xfrom, xyzto);

  private Location a5 = new Location(new UnLocode("AA","AAA"), "AAAAA");
  private Location b5 = new Location(new UnLocode("BB","BBB"), "BBBBB");
  private final CarrierMovement cmAAA_BBB = new CarrierMovement(
          new CarrierMovementId("CAR_001"), a5, b5);
  
  private final Location stockholm = new Location(new UnLocode("SE","STO"), "Stockholm");
  private final Location melbourne = new Location(new UnLocode("AU","MEL"), "Melbourne");
  private final Location hongkong = new Location(new UnLocode("CN","HKG"), "Hongkong");

  protected void setUp() throws Exception{
    service = new HandlingEventServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    eventService = createMock(EventService.class);

    service.setCargoRepository(cargoRepository);
    service.setCarrierMovementRepository(carrierMovementRepository);
    service.setHandlingEventRepository(handlingEventRepository);
    service.setLocationRepository(locationRepository);
    service.setEventService(eventService);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, carrierMovementRepository, handlingEventRepository, eventService);
  }

  public void testRegisterEvent() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    final CarrierMovementId carrierMovementId = new CarrierMovementId("AAA_BBB");
    expect(carrierMovementRepository.find(carrierMovementId)).andReturn(cmAAA_BBB);

    final UnLocode unLocode = new UnLocode("SE", "STO");
    expect(locationRepository.find(unLocode)).andReturn(stockholm);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));
    eventService.fireHandlingEventRegistered(isA(HandlingEvent.class));

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, eventService);
    
    service.register(date, trackingId, carrierMovementId, unLocode, HandlingEvent.Type.LOAD);
  }

  public void testRegisterEventWithoutCarrierMovement() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    handlingEventRepository.save(isA(HandlingEvent.class));
    eventService.fireHandlingEventRegistered(isA(HandlingEvent.class));

    expect(locationRepository.find(stockholm.unLocode())).andReturn(stockholm);

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, eventService);

    service.register(date, trackingId, null, stockholm.unLocode(), HandlingEvent.Type.CLAIM);
  }
  

  public void testRegisterEventInvalidCarrier() throws Exception {
    final Date date = new Date();

    final CarrierMovementId carrierMovementId = new CarrierMovementId("AAA_BBB");
    expect(carrierMovementRepository.find(carrierMovementId)).andReturn(null);

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(new Cargo(trackingId, a5, b5));

    expect(locationRepository.find(melbourne.unLocode())).andReturn(melbourne);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, eventService);
    
    try {
      service.register(date, trackingId, carrierMovementId, melbourne.unLocode(), HandlingEvent.Type.UNLOAD);
      fail("Should not be able to register an event with non-existing carrier movement");
    } catch (UnknownCarrierMovementIdException expected) {}
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(null);

    expect(locationRepository.find(hongkong.unLocode())).andReturn(hongkong);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, eventService);
    
    try {
      service.register(date, trackingId, null, hongkong.unLocode(), HandlingEvent.Type.CLAIM);
      fail("Should not be able to register an event with non-existing cargo");
    } catch (UnknownTrackingIdException expected) {}
  }
  
  public void testRegisterEventInvalidLocation() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(cargoXYZ);
    UnLocode wayOff = new UnLocode("XX", "YYY");
    expect(locationRepository.find(wayOff)).andReturn(null);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository, eventService);
    
    try {
      service.register(date, trackingId, null, wayOff, HandlingEvent.Type.CLAIM);
      fail("Should not be able to register an event with non-existing Location");
    } catch (UnknownLocationException expected) {}
  }
}
