package se.citerus.dddsample.application.routing;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.DateTestUtil;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;
import se.citerus.routingteam.GraphTraversalService;
import se.citerus.routingteam.TransitEdge;
import se.citerus.routingteam.TransitPath;

import static java.util.Arrays.asList;
import java.util.Date;
import java.util.List;

public class ExternalRoutingServiceTest extends TestCase {

  private ExternalRoutingService routingService;
  private GraphTraversalService graphTraversalService;

  @Override
  public void setUp() {
    routingService = new ExternalRoutingService();
    routingService.setVoyageRepository(new VoyageRepositoryInMem());
    routingService.setLocationRepository(new LocationRepositoryInMem());

    graphTraversalService = createMock(GraphTraversalService.class);
    routingService.setGraphTraversalService(graphTraversalService);
  }

  public void testFetchRoutesForSpecification() {
    expect(graphTraversalService.findShortestPath("SESTO", "CNHKG")).
      andReturn(asList(
        new TransitPath(asList(
          new TransitEdge("CM001","SESTO","DEHAM"),
          new TransitEdge("CM002","DEHAM","CNHKG"))),
        new TransitPath(asList(
          new TransitEdge("CM001","SESTO","DEHAM"),
          new TransitEdge("CM006","DEHAM","CNHGH")
        ))
      ));
    replay(graphTraversalService);

    final Date arrivalDeadline = DateTestUtil.toDate("2008-12-01");
    final Cargo cargo = new Cargo(new TrackingId("C123"), STOCKHOLM, HONGKONG);
    final RouteSpecification spec = new RouteSpecification(cargo.origin(), cargo.destination(), arrivalDeadline);

    final List<Itinerary> itineraries = routingService.fetchRoutesForSpecification(spec);
    
    /* TODO implement RouteSpecification, then run these assertions
    assertEquals(1, itineraries.size());

    final Itinerary itinerary = itineraries.get(0);
    assertEquals(2, itinerary.legs().size());
    */
  }

  public void tearDown() {
    verify(graphTraversalService);
  }

}
