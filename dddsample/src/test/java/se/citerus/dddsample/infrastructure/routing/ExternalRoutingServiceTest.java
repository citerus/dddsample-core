package se.citerus.dddsample.infrastructure.routing;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.carrier.SampleVoyages;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.routingteam.GraphTraversalService;
import se.citerus.routingteam.internal.GraphDAO;
import se.citerus.routingteam.internal.GraphTraversalServiceImpl;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ExternalRoutingServiceTest extends TestCase {

  private ExternalRoutingService externalRoutingService;
  private VoyageRepository voyageRepository;

  protected void setUp() throws Exception {
    externalRoutingService = new ExternalRoutingService();
    LocationRepository locationRepository = new LocationRepositoryInMem();
    externalRoutingService.setLocationRepository(locationRepository);

    voyageRepository = createMock(VoyageRepository.class);
    externalRoutingService.setVoyageRepository(voyageRepository);

    GraphTraversalService graphTraversalService = new GraphTraversalServiceImpl(new GraphDAO(createMock(DataSource.class)) {
      public List<String> listLocations() {
        return Arrays.asList(TOKYO.unLocode().idString(), STOCKHOLM.unLocode().idString(), GOTHENBURG.unLocode().idString());
      }

      public void storeCarrierMovementId(String cmId, String from, String to) {
      }
    });
    externalRoutingService.setGraphTraversalService(graphTraversalService);
  }

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, new Date());
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    expect(voyageRepository.find(isA(VoyageNumber.class))).andStubReturn(SampleVoyages.CM002);
    
    replay(voyageRepository);

    List<Itinerary> candidates = externalRoutingService.fetchRoutesForSpecification(routeSpecification);
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
        // Assert that all legs are connected
        assertEquals(legs.get(i).unloadLocation(), legs.get(i + 1).loadLocation());
      }
    }

    verify(voyageRepository);
  }

}
