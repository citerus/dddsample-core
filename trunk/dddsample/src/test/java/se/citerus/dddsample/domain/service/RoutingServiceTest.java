package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.persistence.LocationRepositoryInMem;
import se.citerus.dddsample.application.routing.ExternalRoutingService;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.routingteam.GraphTraversalService;
import se.citerus.routingteam.internal.GraphDAO;
import se.citerus.routingteam.internal.GraphTraversalServiceImpl;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RoutingServiceTest extends TestCase {

  private ExternalRoutingService routingService;
  private CarrierMovementRepository carrierMovementRepository;

  protected void setUp() throws Exception {
    routingService = new ExternalRoutingService();
    LocationRepository locationRepository = new LocationRepositoryInMem();
    routingService.setLocationRepository(locationRepository);

    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    routingService.setCarrierMovementRepository(carrierMovementRepository);

    GraphTraversalService graphTraversalService = new GraphTraversalServiceImpl(new GraphDAO(createMock(DataSource.class)) {
      public List<String> listLocations() {
        return Arrays.asList(TOKYO.unLocode().idString(), STOCKHOLM.unLocode().idString(), GOTHENBURG.unLocode().idString());
      }

      public void storeCarrierMovementId(String cmId, String from, String to) {
      }
    });
    routingService.setGraphTraversalService(graphTraversalService);
  }

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    Cargo cargo = new Cargo(trackingId, HONGKONG, HELSINKI);
    RouteSpecification routeSpecification = RouteSpecification.forCargo(cargo, new Date());

    expect(carrierMovementRepository.find(isA(CarrierMovementId.class))).
      andStubReturn(new CarrierMovement(new CarrierMovementId("CM"), CHICAGO, HAMBURG, new Date(), new Date()));
    
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
