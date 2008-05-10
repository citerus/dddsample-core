package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.SampleLocations.HONGKONG;
import se.citerus.dddsample.repository.LocationRepository;

import java.util.List;
import java.util.Set;

public class RoutingServiceTest extends TestCase {

  RoutingServiceImpl routingService;
  LocationRepository locationRepository;

  protected void setUp() throws Exception {
    routingService = new RoutingServiceImpl();
    locationRepository = createMock(LocationRepository.class);
    routingService.setLocationRepository(locationRepository);
  }

  public void testCalculatePossibleRoutes() {
    expect(locationRepository.findAll()).andStubReturn(SampleLocations.getAll());
    replay(locationRepository);

    Cargo cargo = new Cargo(new TrackingId("ABC"), HONGKONG, HELSINKI);
    Set<Itinerary> candidates = routingService.calculatePossibleRoutes(cargo, null);
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


    verify(locationRepository);
  }

}
