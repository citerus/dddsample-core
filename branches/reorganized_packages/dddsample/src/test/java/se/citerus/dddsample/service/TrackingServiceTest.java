package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.repository.CargoRepository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class TrackingServiceTest extends TestCase {

  TrackingServiceImpl cargoService;
  CargoRepository cargoRepository;

  protected void setUp() throws Exception {
    cargoService = new TrackingServiceImpl();
    cargoRepository = createMock(CargoRepository.class);

    cargoService.setCargoRepository(cargoRepository);
  }

  public void testTrackingScenario() {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, CHICAGO);

    HandlingEvent claimed = new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.CLAIM, STOCKHOLM, null);
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), STOCKHOLM, CHICAGO);
    HandlingEvent loaded = new HandlingEvent(cargo, new Date(12), new Date(25), HandlingEvent.Type.LOAD, STOCKHOLM, carrierMovement);
    HandlingEvent unloaded = new HandlingEvent(cargo, new Date(100), new Date(110), HandlingEvent.Type.UNLOAD, CHICAGO, carrierMovement);
    // Add out of order to verify ordering in DTO
    List<HandlingEvent> eventList = Arrays.asList(loaded, unloaded, claimed);
    cargo.setDeliveryHistory(new DeliveryHistory(eventList));

    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(cargo);

    replay(cargoRepository);


    // Tested call
    Cargo trackedCargo = cargoService.track(new TrackingId("XYZ"));

    assertEquals(cargo, trackedCargo);

    List<HandlingEvent> events = trackedCargo.deliveryHistory().eventsOrderedByCompletionTime();
    assertEquals(3, events.size());

    // Claim happened first
    HandlingEvent handlingEvent = events.get(0);
    assertEquals(claimed, handlingEvent);

    // Then load
    handlingEvent = events.get(1);
    assertEquals(loaded, handlingEvent);

    // Finally unload
    handlingEvent = events.get(2);
    assertEquals(unloaded, handlingEvent);
  }

  public void testTrackNullResult() {
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(null);
    replay(cargoRepository);

    // Tested call
    Cargo cargo = cargoService.track(new TrackingId("XYZ"));
    
    assertNull(cargo);
  }

  protected void onTearDown() throws Exception {
    verify(cargoRepository);
  }

}
