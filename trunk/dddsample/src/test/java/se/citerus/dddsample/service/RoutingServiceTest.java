package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.SampleLocations.HONGKONG;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.List;

public class RoutingServiceTest extends TestCase {

  RoutingServiceImpl routingService;
  LocationRepository locationRepository;
  CargoRepository cargoRepository;

  protected void setUp() throws Exception {
    routingService = new RoutingServiceImpl();
    locationRepository = createMock(LocationRepository.class);
    routingService.setLocationRepository(locationRepository);
    cargoRepository = createMock(CargoRepository.class);
    routingService.setCargoRepository(cargoRepository);
  }

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    Cargo cargo = new Cargo(trackingId, HONGKONG, HELSINKI);

    expect(locationRepository.findAll()).andStubReturn(SampleLocations.getAll());
    expect(cargoRepository.find(isA(TrackingId.class))).andReturn(cargo);
    replay(locationRepository, cargoRepository);

    List<Itinerary> candidates = routingService.calculatePossibleRoutes(trackingId, null);
    assertNotNull(candidates);
    
    for (Itinerary itinerary : candidates) {
      List<Leg> legs = itinerary.legs();
      assertNotNull(legs);
      assertFalse(legs.isEmpty());

      // Cargo origin and start of first leg should match
      Location firstLegStart = legs.get(0).from();
      assertEquals(cargo.origin(), firstLegStart);

      // Cargo final destination and last leg stop should match
      Location lastLegStop = legs.get(legs.size() - 1).to();
      assertEquals(cargo.finalDestination(), lastLegStop);

      for (int i = 0; i < legs.size() - 1; i++) {
        // Assert that all legs are conencted
        assertEquals(legs.get(i).to(), legs.get(i + 1).from());
      }
    }

    verify(locationRepository, cargoRepository);
  }

}
