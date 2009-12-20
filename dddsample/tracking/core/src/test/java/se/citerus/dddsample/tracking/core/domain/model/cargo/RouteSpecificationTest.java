package se.citerus.dddsample.tracking.core.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

public class RouteSpecificationTest extends TestCase {

  final Voyage hongKongTokyoNewYork = new Voyage.Builder(
    new VoyageNumber("V001"), HONGKONG).
    addMovement(TOKYO, toDate("2009-02-01"), toDate("2009-02-05")).
    addMovement(NEWYORK, toDate("2009-02-06"), toDate("2009-02-10")).
    addMovement(HONGKONG, toDate("2009-02-11"), toDate("2009-02-14")).
    build();

  final Voyage dallasNewYorkChicago = new Voyage.Builder(
    new VoyageNumber("V002"), DALLAS).
    addMovement(NEWYORK, toDate("2009-02-06"), toDate("2009-02-07")).
    addMovement(CHICAGO, toDate("2009-02-12"), toDate("2009-02-20")).
    build();

  final Itinerary itinerary = new Itinerary(
    Leg.deriveLeg(hongKongTokyoNewYork, HONGKONG, NEWYORK),
    Leg.deriveLeg(dallasNewYorkChicago, NEWYORK, CHICAGO)
  );

  public void testIsSatisfiedBy_Success() {
    RouteSpecification routeSpecification = new RouteSpecification(
      HONGKONG, CHICAGO, toDate("2009-03-01")
    );

    assertTrue(routeSpecification.isSatisfiedBy(itinerary));
  }

  public void testIsSatisfiedBy_WrongOrigin() {
    RouteSpecification routeSpecification = new RouteSpecification(
      HANGZOU, CHICAGO, toDate("2009-03-01")
    );

    assertFalse(routeSpecification.isSatisfiedBy(itinerary));
  }

  public void testIsSatisfiedBy_WrongDestination() {
    RouteSpecification routeSpecification = new RouteSpecification(
      HONGKONG, DALLAS, toDate("2009-03-01")
    );

    assertFalse(routeSpecification.isSatisfiedBy(itinerary));
  }

  public void testIsSatisfiedBy_MissedDeadline() {
    RouteSpecification routeSpecification = new RouteSpecification(
      HONGKONG, CHICAGO, toDate("2009-02-15")
    );

    assertFalse(routeSpecification.isSatisfiedBy(itinerary));
  }

  public void testEquals() {
    RouteSpecification HKG_DAL = new RouteSpecification(
      HONGKONG, DALLAS, toDate("2009-03-01")
    );
    RouteSpecification HKG_DAL_AGAIN = new RouteSpecification(
      HONGKONG, DALLAS, toDate("2009-03-01")
    );
    RouteSpecification SHA_DAL = new RouteSpecification(
      SHANGHAI, DALLAS, toDate("2009-03-01")
    );
    RouteSpecification HKG_CHI = new RouteSpecification(
      HONGKONG, CHICAGO, toDate("2009-03-01")
    );
    RouteSpecification HKG_DAL_LATERARRIVAL = new RouteSpecification(
      HONGKONG, DALLAS, toDate("2009-03-15")
    );

    assertEquals(HKG_DAL, HKG_DAL_AGAIN);
    assertFalse(HKG_DAL.equals(SHA_DAL));
    assertFalse(HKG_DAL.equals(HKG_CHI));
    assertFalse(HKG_DAL.equals(HKG_DAL_LATERARRIVAL));
  }

  public void testDeriveWithNewDestination() {
    RouteSpecification original = new RouteSpecification(
      HONGKONG, DALLAS, toDate("2009-03-01")
    );
    RouteSpecification desired = new RouteSpecification(
      HONGKONG, CHICAGO, toDate("2009-03-01")
    );
    assertEquals(desired, original.withDestination(CHICAGO));
  }

}
