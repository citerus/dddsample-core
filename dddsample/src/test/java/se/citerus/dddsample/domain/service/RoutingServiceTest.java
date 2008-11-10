package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.persistence.LocationRepositoryInMem;
import se.citerus.dddsample.application.routing.ExternalRoutingService;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.carrier.SampleVoyages;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
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
  private VoyageRepository voyageRepository;

  protected void setUp() throws Exception {
    routingService = new ExternalRoutingService();
    LocationRepository locationRepository = new LocationRepositoryInMem();
    routingService.setLocationRepository(locationRepository);

    voyageRepository = createMock(VoyageRepository.class);
    routingService.setCarrierMovementRepository(voyageRepository);

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
    RouteSpecification routeSpecification = new RouteSpecification(cargo.origin(), cargo.destination(), new Date());

    expect(voyageRepository.find(isA(VoyageNumber.class))).andStubReturn(SampleVoyages.CM002);
    
    replay(voyageRepository);

    List<Itinerary> candidates = routingService.fetchRoutesForSpecification(routeSpecification);
    assertNotNull(candidates);
    
    for (Itinerary itinerary : candidates) {
      List<Leg> legs = itinerary.legs();
      assertNotNull(legs);
      assertFalse(legs.isEmpty());

      // Cargo origin and start of first leg should match
      assertEquals(cargo.origin(), legs.get(0).loadLocation());

      // Cargo final destination and last leg stop should match
      Location lastLegStop = legs.get(legs.size() - 1).unloadLocation();
      assertEquals(cargo.destination(), lastLegStop);

      for (int i = 0; i < legs.size() - 1; i++) {
        // Assert that all legs are conencted
        assertEquals(legs.get(i).unloadLocation(), legs.get(i + 1).loadLocation());
      }
    }

    verify(voyageRepository);
  }

}
