/**
 * Purpose
 * @author peter
 * @created 2009-aug-05
 * $Id$
 */
package se.citerus.dddsample.scenario;

import junit.framework.TestCase;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.Date;

public class VoyageRescheduledScenarioTest extends TestCase {

  public void testVoyageRescheduled() throws Exception {
    // Creating new voyages to avoid rescheduling shared ones, breaking other tests
    Voyage voyage1 = new Voyage(new VoyageNumber("V1"), HONGKONG_TO_NEW_YORK.schedule());
    Voyage voyage2 = new Voyage(new VoyageNumber("V2"), NEW_YORK_TO_DALLAS.schedule());
    Voyage voyage3 = new Voyage(new VoyageNumber("V3"), DALLAS_TO_HELSINKI.schedule());

    Itinerary itinerary = new Itinerary(
      Leg.deriveLeg(voyage1, HANGZOU, NEWYORK),
      Leg.deriveLeg(voyage2, NEWYORK, DALLAS),
      Leg.deriveLeg(voyage3, DALLAS, STOCKHOLM)
    );

    Date oldDepartureTime = toDate("2008-10-24", "07:00");

    assertEquals(itinerary.legs().get(1).loadTime(), oldDepartureTime);
    assertEquals(voyage2.schedule().carrierMovements().get(0).departureTime(), oldDepartureTime);

    Date newDepartureTime = toDate("2008-10-24", "18:00");

    voyage2.departureRescheduled(NEWYORK, newDepartureTime);
    Itinerary newItinerary = itinerary.withRescheduledVoyage(voyage2);

    assertEquals(voyage2.schedule().carrierMovements().get(0).departureTime(), newDepartureTime);
    assertEquals(newItinerary.legs().get(1).loadTime(), newDepartureTime);
  }

}
