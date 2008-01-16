package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;

import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  
  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new Location("ABCFROM"), new Location("ABCTO"));
  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), new Location("XYZFROM"), new Location("XYZTO"));
  private final CarrierMovement cmAAA_BBB = new CarrierMovement(
          new CarrierMovementId("CAR_001"), new Location("AAA"), new Location("BBB"));

  protected void setUp() throws Exception{
    service = new HandlingEventServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    
    service.setCargoRepository(cargoRepository);
    service.setCarrierRepository(carrierMovementRepository);
    service.setHandlingEventRepository(handlingEventRepository);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, carrierMovementRepository, handlingEventRepository);
  }

  public void testRegisterEvent() throws Exception {
    final String carrierMovementId = "AAA_BBB";
    final String[] trackingIds = { "ABC", "XYZ" };
    final Date date = new Date();

    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(cargoXYZ);
    expect(carrierMovementRepository.find(new CarrierMovementId("AAA_BBB"))).andReturn(cmAAA_BBB);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));
    expectLastCall().times(2);  // Two tracking ids

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository);
    
    service.registerUnload(date, carrierMovementId, trackingIds);
  }
  
  public void testRegisterEventInvalidCarrier() throws Exception {
    final String[] trackingIds = { "ABC", "XYZ" };
    final Date date = new Date();

    expect(carrierMovementRepository.find(new CarrierMovementId("AAA_BBB"))).andReturn(null);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository);
    
    try {
      service.registerUnload(date, "AAA_BBB", trackingIds);
      fail("Should not be able to register an event with non-existing carrier movement");
    } catch (IllegalArgumentException expected) {}
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final String[] trackIds = { "ABC", "XYZ" };
    final Date date = new Date();

    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(null);
    expect(carrierMovementRepository.find(new CarrierMovementId("AAA_BBB"))).andReturn(cmAAA_BBB);
    handlingEventRepository.save(isA(HandlingEvent.class));

    replay(cargoRepository, carrierMovementRepository, handlingEventRepository);
    
    try {
      service.registerUnload(date, "AAA_BBB", trackIds);
      fail("Should not be able to register an event with non-existing cargo");
    } catch (IllegalArgumentException expected) {}
  }
}
