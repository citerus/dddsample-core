package se.citerus.dddsample.tracking.core.infrastructure.routing;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.api.TransitPath;
import junit.framework.TestCase;
import org.easymock.EasyMock;
import static org.easymock.EasyMock.*;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HONGKONG;
import se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.LocationRepositoryInMem;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ExternalRoutingServiceTest extends TestCase {

  private ExternalRoutingService externalRoutingService;
  private VoyageRepository voyageRepository;

  protected void setUp() throws Exception {
    LocationRepository locationRepository = new LocationRepositoryInMem();
    voyageRepository = createMock(VoyageRepository.class);

    GraphTraversalService graphTraversalService = EasyMock.createMock(GraphTraversalService.class);
    expect(
      graphTraversalService.findShortestPath(isA(String.class), isA(String.class), isA(Properties.class))).
      andStubReturn(new ArrayList<TransitPath>());
    
    EasyMock.replay(graphTraversalService);
    // TODO expectations on GTS
    externalRoutingService = new ExternalRoutingService(graphTraversalService, locationRepository, voyageRepository);

      /*new GraphTraversalServiceImpl(new GraphDAO() {
      public List<String> listLocations() {
        return Arrays.asList(TOKYO.unLocode().stringValue(), STOCKHOLM.unLocode().stringValue(), GOTHENBURG.unLocode().stringValue());
      }

      public void storeCarrierMovementId(String cmId, String from, String to) {
      }
    });*/
  }

  // TODO this test belongs in com.pathfinder

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, toDate("2009-04-01"));
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    expect(voyageRepository.find(isA(VoyageNumber.class))).andStubReturn(SampleVoyages.pacific2);

    replay(voyageRepository);

    List<Itinerary> candidates = externalRoutingService.fetchRoutesForSpecification(routeSpecification);
    assertNotNull(candidates);

    for (Itinerary itinerary : candidates) {
      List<Leg> legs = itinerary.legs();
      assertNotNull(legs);
      assertFalse(legs.isEmpty());

      // Cargo origin and start of first leg should match
      assertEquals(cargo.routeSpecification().origin(), legs.get(0).loadLocation());

      // Cargo final destination and last leg stop should match
      Location lastLegStop = legs.get(legs.size() - 1).unloadLocation();
      assertEquals(cargo.routeSpecification().destination(), lastLegStop);

      for (int i = 0; i < legs.size() - 1; i++) {
        // Assert that all legs are connected
        assertEquals(legs.get(i).unloadLocation(), legs.get(i + 1).loadLocation());
      }
    }

    verify(voyageRepository);
  }

}
