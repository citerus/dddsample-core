package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;

import java.util.Date;

public class RouteSpecificationTest extends TestCase {

  public void testIsSatisfiedBySuccess() {
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, CHICAGO, new Date());
    Itinerary itinerary = new Itinerary();
    assertTrue(routeSpecification.isSatisfiedBy(itinerary));
  }

  public void testIsSatisfiedByInvalidDate() {
    // TODO
  }

  public void testIsSatisfiedByInvalidOrigin() {
    // TODO
  }

  public void testIsSatisfiedByInvalidDestination() {
    // TODO
  }

}
