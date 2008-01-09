package se.citerus.dddsample.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierRepository;
import se.citerus.dddsample.repository.HandlingEventRepository;
import static org.easymock.EasyMock.*;

public class HandlingEventServiceTest extends TestCase {
  private HandlingEventServiceImpl service;
  private CargoRepository cargoRepository;
  private CarrierRepository carrierRepository;
  private HandlingEventRepository handlingEventRepository;
  
  private final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new Location("ABCFROM"), new Location("ABCTO"));
  private final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), new Location("XYZFROM"), new Location("XYZTO"));
  private final CarrierMovement cmAAA_BBB = new CarrierMovement(new Location("AAA"), new Location("BBB"));

  protected void setUp() throws Exception{
    service = new HandlingEventServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    carrierRepository = createMock(CarrierRepository.class);
    handlingEventRepository = createMock(HandlingEventRepository.class);
    
    service.setCargoRepository(cargoRepository);
    service.setCarrierRepository(carrierRepository);
    service.setHandlingEventRepository(handlingEventRepository);
  }

  public void testRegisterEvent() throws Exception {
    String carrierId = "AAA_BBB";
    final String[] trackIds = { "ABC", "XYZ" };
    Date date = Calendar.getInstance().getTime();
    String type = "UNLOAD";
    
    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(cargoXYZ);
    expect(carrierRepository.find("AAA_BBB")).andReturn(cmAAA_BBB);
    
    HandlingEvent event = new HandlingEvent(date, HandlingEvent.parseType(type), cmAAA_BBB);
    Set<Cargo> cargos = new HashSet<Cargo>();
    cargos.add(cargoABC);
    cargos.add(cargoXYZ);
    event.register(cargos);
    
    handlingEventRepository.save(event);
    
    replay(cargoRepository, carrierRepository, handlingEventRepository);
    
    service.register(date, type, carrierId, trackIds);
    
    verify(cargoRepository, carrierRepository, handlingEventRepository);
  }
  
  public void testRegisterEventInvalidCarrier() throws Exception {
    final String[] trackIds = { "ABC", "XYZ" };
    Date date = Calendar.getInstance().getTime();
    String type = "UNLOAD";
    
    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(cargoXYZ);
    expect(carrierRepository.find("AAA_BBB")).andReturn(null);
    
    replay(cargoRepository, carrierRepository, handlingEventRepository);
    
    try {
      service.register(date, type, "AAA_BBB", trackIds);
      assertFalse(true);
    } catch (IllegalArgumentException e) {
      // Expected IllegalArgumentExecption
    }
  }
  
  public void testRegisterEventInvalidCargo() throws Exception {
    final String[] trackIds = { "ABC", "XYZ" };
    Date date = Calendar.getInstance().getTime();
    String type = "UNLOAD";
    
    expect(cargoRepository.find(new TrackingId("ABC"))).andReturn(cargoABC);
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(null);
    expect(carrierRepository.find("AAA_BBB")).andReturn(cmAAA_BBB);
    
    replay(cargoRepository, carrierRepository, handlingEventRepository);
    
    try {
      service.register(date, type, "AAA_BBB", trackIds);
      assertFalse(true);
    } catch (IllegalArgumentException e) {
      // Expected IllegalArgumentExecption
    }
  }
}
