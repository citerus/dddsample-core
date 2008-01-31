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
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  private LocationRepository locationRepository;
  
  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new Location("AFROM"), new Location("ABCTO"));
  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), new Location("XFROM"), new Location("XYZTO"));
  private final CarrierMovement cmAAA_BBB = new CarrierMovement(
          new CarrierMovementId("CAR_001"), new Location("AAAAA"), new Location("BBBBB"));
  
  private final Location locationSESTO = new Location("SESTO");
  private final Location locationAUMEL = new Location("AUMEL");
  private final Location locationCNHKG = new Location("CNHKG");

  protected void setUp() throws Exception{
    service = new HandlingEventServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    locationRepository = createMock(LocationRepository.class);
    
    service.setCargoRepository(cargoRepository);
    service.setCarrierRepository(carrierMovementRepository);
    service.setHandlingEventRepository(handlingEventRepository);
    service.setLocationRepository(locationRepository);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, carrierMovementRepository, handlingEventRepository);
  }

  public void testRegisterEvent() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    final CarrierMovementId carrierMovementId = new CarrierMovementId("AAA_BBB");
    expect(carrierMovementRepository.find(carrierMovementId)).andReturn(cmAAA_BBB);
    
    expect(locationRepository.find("SESTO")).andReturn(locationSESTO);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository);
    
    service.register(date, trackingId, carrierMovementId, "SESTO", HandlingEvent.Type.LOAD);
  }

  public void testRegisterEventWithoutCarrierMovement() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("ABC");
    expect(cargoRepository.find(trackingId)).andReturn(cargoABC);

    handlingEventRepository.save(isA(HandlingEvent.class));
    
    expect(locationRepository.find("SESTO")).andReturn(locationSESTO);

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository);

    service.register(date, trackingId, null, "SESTO", HandlingEvent.Type.CLAIM);
  }
  

  public void testRegisterEventInvalidCarrier() throws Exception {
    final Date date = new Date();

    final CarrierMovementId carrierMovementId = new CarrierMovementId("AAA_BBB");
    expect(carrierMovementRepository.find(carrierMovementId)).andReturn(null);

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(new Cargo(trackingId, new Location("FROMX"), new Location("TOYYY")));

    expect(locationRepository.find("AUMEL")).andReturn(locationAUMEL);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository);
    
    try {
      service.register(date, trackingId, carrierMovementId, "AUMEL", HandlingEvent.Type.UNLOAD);
      fail("Should not be able to register an event with non-existing carrier movement");
    } catch (UnknownCarrierMovementIdException expected) {}
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(null);

    expect(locationRepository.find("CNHKG")).andReturn(locationCNHKG);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository);
    
    try {
      service.register(date, trackingId, null, "CNHKG", HandlingEvent.Type.CLAIM);
      fail("Should not be able to register an event with non-existing cargo");
    } catch (UnknownTrackingIdException expected) {}
  }
  
  public void testRegisterEventInvalidLocation() throws Exception {
    final Date date = new Date();

    final TrackingId trackingId = new TrackingId("XYZ");
    expect(cargoRepository.find(trackingId)).andReturn(cargoXYZ);
    expect(locationRepository.find("WAY_OFF")).andReturn(null);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository, locationRepository);
    
    try {
      service.register(date, trackingId, null, "WAY_OFF", HandlingEvent.Type.CLAIM);
      fail("Should not be able to register an event with non-existing Location");
    } catch (UnknownLocationException expected) {}
  }
}
