package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.*;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.repository.LocationRepositoryInMem;

import java.util.Date;
import java.util.List;

public class RoutingServiceTest extends TestCase {

  private RoutingServiceImpl routingService;
  private CarrierMovementRepository carrierMovementRepository;

  protected void setUp() throws Exception {
    routingService = new RoutingServiceImpl();
    LocationRepository locationRepository = new LocationRepositoryInMem();
    routingService.setLocationRepository(locationRepository);

    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    routingService.setCarrierMovementRepository(carrierMovementRepository);

    GraphTraversalService graphTraversalService = new GraphTraversalService();
    graphTraversalService.setCarrierMovementRepository(carrierMovementRepository);
    graphTraversalService.setLocationRepository(locationRepository);
    routingService.setGraphTraversalService(graphTraversalService);
  }

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    Cargo cargo = new Cargo(trackingId, HONGKONG, HELSINKI);
    RouteSpecification routeSpecification = RouteSpecification.forCargo(cargo, new Date());

    expect(carrierMovementRepository.find(isA(CarrierMovementId.class))).
      andStubReturn(new CarrierMovement(new CarrierMovementId("CM"), CHICAGO, HAMBURG));
    
    replay(carrierMovementRepository);

    List<Itinerary> candidates = routingService.fetchRoutesForSpecification(routeSpecification);
    assertNotNull(candidates);
    
    for (Itinerary itinerary : candidates) {
      List<Leg> legs = itinerary.legs();
      assertNotNull(legs);
      assertFalse(legs.isEmpty());

      // Cargo origin and start of first leg should match
      assertEquals(cargo.origin(), legs.get(0).from());

      // Cargo final destination and last leg stop should match
      Location lastLegStop = legs.get(legs.size() - 1).to();
      assertEquals(cargo.destination(), lastLegStop);

      for (int i = 0; i < legs.size() - 1; i++) {
        // Assert that all legs are conencted
        assertEquals(legs.get(i).to(), legs.get(i + 1).from());
      }
    }

    verify(carrierMovementRepository);
  }

}
