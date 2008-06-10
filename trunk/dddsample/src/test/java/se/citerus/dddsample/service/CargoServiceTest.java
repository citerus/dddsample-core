package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.dto.CargoTrackingDTO;
import se.citerus.dddsample.service.dto.HandlingEventDTO;

import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class CargoServiceTest extends TestCase {

  CargoServiceImpl cargoService;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;

  protected void setUp() throws Exception {
    cargoService = new CargoServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    locationRepository = createMock(LocationRepository.class);

    cargoService.setCargoRepository(cargoRepository);
    cargoService.setLocationRepository(locationRepository);
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
    CargoTrackingDTO cargoDTO = cargoService.track(new TrackingId("XYZ"));


    assertEquals("XYZ", cargoDTO.getTrackingId());
    assertEquals("SESTO (Stockholm)", cargoDTO.getOrigin());
    assertEquals("USCHI (Chicago)", cargoDTO.getFinalDestination());
    assertEquals("USCHI", cargoDTO.getCurrentLocationId());

    List<HandlingEventDTO> events = cargoDTO.getEvents();
    assertEquals(3, events.size());

    // Claim happened first
    HandlingEventDTO eventDTO = events.get(0);
    assertEquals("SESTO (Stockholm)", eventDTO.getLocation());
    assertEquals("CLAIM", eventDTO.getType());
    assertEquals("", eventDTO.getCarrier());
    assertEquals(new Date(10), eventDTO.getTime());

    // Then load
    eventDTO = events.get(1);
    assertEquals("SESTO (Stockholm)", eventDTO.getLocation());
    assertEquals("LOAD", eventDTO.getType());
    assertEquals("CAR_001", eventDTO.getCarrier());
    assertEquals(new Date(12), eventDTO.getTime());

    // Finally unload
    eventDTO = events.get(2);
    assertEquals("USCHI (Chicago)", eventDTO.getLocation());
    assertEquals("UNLOAD", eventDTO.getType());
    assertEquals("CAR_001", eventDTO.getCarrier());
    assertEquals(new Date(100), eventDTO.getTime());
  }

  public void testTrackNullResult() {
    expect(cargoRepository.find(new TrackingId("XYZ"))).andReturn(null);
    replay(cargoRepository);

    // Tested call
    CargoTrackingDTO cargoDTO = cargoService.track(new TrackingId("XYZ"));
    
    assertNull(cargoDTO);
  }

  public void testRegisterNew() {
    TrackingId expectedTrackingId = new TrackingId("TRK1");
    UnLocode fromUnlocode = new UnLocode("USCHI");
    UnLocode toUnlocode = new UnLocode("SESTO");

    expect(cargoRepository.nextTrackingId()).andReturn(expectedTrackingId);
    expect(locationRepository.find(fromUnlocode)).andReturn(CHICAGO);
    expect(locationRepository.find(toUnlocode)).andReturn(STOCKHOLM);

    cargoRepository.save(isA(Cargo.class));

    replay(cargoRepository, locationRepository);
    
    TrackingId trackingId = cargoService.registerNewCargo(fromUnlocode, toUnlocode);
    assertEquals(expectedTrackingId, trackingId);
  }

  public void testRegisterNewNullArguments() {
    try {
      cargoService.registerNewCargo(null, null);
      fail("Null arguments should not be allowed");
    } catch (IllegalArgumentException expected) {}
  }

  protected void onTearDown() throws Exception {
    verify(cargoRepository, locationRepository);
  }

}
