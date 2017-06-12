package se.citerus.dddsample.infrastructure.routing;

import com.pathfinder.api.GraphTraversalService;
import com.pathfinder.internal.GraphDAO;
import com.pathfinder.internal.GraphTraversalServiceImpl;
import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

public class ExternalRoutingServiceTest extends TestCase {

  private ExternalRoutingService externalRoutingService;
  private VoyageRepository voyageRepository;

  protected void setUp() throws Exception {
    externalRoutingService = new ExternalRoutingService();
    LocationRepository locationRepository = new LocationRepositoryInMem();
    externalRoutingService.setLocationRepository(locationRepository);

    voyageRepository = createMock(VoyageRepository.class);
    externalRoutingService.setVoyageRepository(voyageRepository);

    GraphTraversalService graphTraversalService = new GraphTraversalServiceImpl(new GraphDAO() {
      public List<String> listLocations() {
        return Arrays.asList(TOKYO.unLocode().idString(), STOCKHOLM.unLocode().idString(), GOTHENBURG.unLocode().idString());
      }

      public void storeCarrierMovementId(String cmId, String from, String to) {
      }
    });
    externalRoutingService.setGraphTraversalService(graphTraversalService);
  }

  // TODO this test belongs in com.pathfinder

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, new Date());
    Cargo cargo = new Cargo(trackingId, routeSpecification);

    expect(voyageRepository.find(isA(VoyageNumber.class))).andStubReturn(SampleVoyages.CM002);
    
    replay(voyageRepository);

    List<Itinerary> candidates = externalRoutingService.fetchRoutesForSpecification(routeSpecification);
    assertThat(candidates).isNotNull();

    for (Itinerary itinerary : candidates) {
      List<Leg> legs = itinerary.legs();
      assertThat(legs).isNotNull();
      assertThat(legs.isEmpty()).isFalse();

      // Cargo origin and start of first leg should match
      assertThat(legs.get(0).loadLocation()).isEqualTo(cargo.origin());

      // Cargo final destination and last leg stop should match
      Location lastLegStop = legs.get(legs.size() - 1).unloadLocation();
      assertThat(lastLegStop).isEqualTo(cargo.routeSpecification().destination());

      for (int i = 0; i < legs.size() - 1; i++) {
        // Assert that all legs are connected
        assertThat(legs.get(i + 1).loadLocation()).isEqualTo(legs.get(i).unloadLocation());
      }
    }

    verify(voyageRepository);
  }

}
