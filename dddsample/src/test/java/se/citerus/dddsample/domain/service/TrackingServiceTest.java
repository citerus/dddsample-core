package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.CargoTestHelper;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.SampleVoyages;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.domain.service.impl.TrackingServiceImpl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class TrackingServiceTest extends TestCase {

  TrackingServiceImpl cargoService;
  CargoRepository cargoRepository;
  DomainEventNotifier domainEventNotifier;

  protected void setUp() throws Exception {
    cargoRepository = createMock(CargoRepository.class);
    domainEventNotifier = createMock(DomainEventNotifier.class);
    cargoService = new TrackingServiceImpl(domainEventNotifier, cargoRepository);
  }

  public void testTrackingScenario() {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, CHICAGO);

    HandlingEvent claimed = new HandlingEvent(cargo, new Date(10), new Date(20), HandlingEvent.Type.CLAIM, STOCKHOLM);

    HandlingEvent loaded = new HandlingEvent(cargo, new Date(12), new Date(25), HandlingEvent.Type.LOAD, STOCKHOLM, SampleVoyages.CM001);
    HandlingEvent unloaded = new HandlingEvent(cargo, new Date(100), new Date(110), HandlingEvent.Type.UNLOAD, CHICAGO, SampleVoyages.CM002);

    // Add out of order to verify ordering in DTO
    List<HandlingEvent> eventList = Arrays.asList(loaded, unloaded, claimed);

    CargoTestHelper.setDeliveryHistory(cargo, eventList);

    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(cargo);

    replay(cargoRepository);


    // Tested call
    Cargo trackedCargo = cargoService.track(new TrackingId("XYZ"));

    assertEquals(cargo, trackedCargo);

    List<HandlingEvent> events = trackedCargo.deliveryHistory().history();
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
