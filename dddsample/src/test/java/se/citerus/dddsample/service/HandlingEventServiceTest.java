package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;

import java.util.Calendar;
import java.util.Date;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;
  private HandlingEventRepository handlingEventRepository;
  
  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new Location("ABCFROM"), new Location("ABCTO"));
  private final CarrierMovement cmAAA_BBB = new CarrierMovement(
          new CarrierId("CAR_001"), new Location("AAA"), new Location("BBB"));

  protected void setUp() throws Exception{
    service = new HandlingEventServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    
    service.setCargoRepository(cargoRepository);
    service.setCarrierRepository(carrierMovementRepository);
    service.setHandlingEventRepository(handlingEventRepository);
  }

  public void testRegisterEvent() throws Exception {
    String carrierId = "AAA_BBB";
    final String trackId = "ABC";
    Date date = Calendar.getInstance().getTime();
    String type = "UNLOAD";
    
    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(carrierMovementRepository.find(new CarrierId("AAA_BBB"))).andReturn(cmAAA_BBB);

    // TODO: does not inspect the handling event instance in a sufficient way
    handlingEventRepository.save(isA(HandlingEvent.class));
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository);
    
    service.register(date, type, new Location("ABCTO"), carrierId, trackId);
    
    verify(cargoRepository, carrierMovementRepository, handlingEventRepository);
  }
  
  public void testRegisterEventInvalidCarrier() throws Exception {
    final String trackId = "ABC";
    Date date = Calendar.getInstance().getTime();
    String type = "UNLOAD";
    
    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(carrierMovementRepository.find(new CarrierId("AAA_BBB"))).andReturn(null);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository);
    
    try {
      service.register(date, type, new Location("BBB"), "AAA_BBB", trackId);
      assertFalse(true);
    } catch (IllegalArgumentException e) {
      // Expected IllegalArgumentExecption
    }
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final String trackId =  "XYZ";
    Date date = Calendar.getInstance().getTime();
    String type = "UNLOAD";
    
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(null);
    expect(carrierMovementRepository.find(new CarrierId("AAA_BBB"))).andReturn(cmAAA_BBB);
    
    replay(cargoRepository, carrierMovementRepository, handlingEventRepository);
    
    try {
      service.register(date, type,  new Location("BBB"), "AAA_BBB", trackId);
      assertFalse(true);
    } catch (IllegalArgumentException e) {
      // Expected IllegalArgumentExecption
    }
  }
}
