/**
 * Purpose
 * @author peter
 * @created 2009-aug-05
 * $Id$
 */
package se.citerus.dddsample.scenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.Before;
import org.junit.Test;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoFactory;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.MISROUTED;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.infrastructure.persistence.TrackingIdGeneratorInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;

import java.util.Date;

public class VoyageRescheduledScenarioTest {

  Cargo cargo;
  Voyage voyage1;
  Voyage voyage2;
  Voyage voyage3;

  @Before
  public void setupCargo() {
    // Creating new voyages to avoid rescheduling shared ones, breaking other tests
    voyage1 = new Voyage(new VoyageNumber("V1"), HONGKONG_TO_NEW_YORK.schedule());
    voyage2 = new Voyage(new VoyageNumber("V2"), NEW_YORK_TO_DALLAS.schedule());
    voyage3 = new Voyage(new VoyageNumber("V3"), DALLAS_TO_HELSINKI.schedule());
    CargoFactory cargoFactory = new CargoFactory(new LocationRepositoryInMem(), new TrackingIdGeneratorInMem());
    cargo = cargoFactory.newCargo(HANGZOU.unLocode(), STOCKHOLM.unLocode(), toDate("2008-12-23"));
    Itinerary itinerary = new Itinerary(
      Leg.deriveLeg(voyage1, HANGZOU, NEWYORK),
      Leg.deriveLeg(voyage2, NEWYORK, DALLAS),
      Leg.deriveLeg(voyage3, DALLAS, STOCKHOLM)
    );
    cargo.assignToRoute(itinerary);
  }

  @Test
  public void voyageIsRescheduledWithMaintainableRoute() {
    assertThat(cargo.routingStatus(), is(ROUTED));

    Date oldDepartureTime = toDate("2008-10-24", "07:00");

    assertThat(voyage2.schedule().departureTimeAt(NEWYORK), is(oldDepartureTime));
    assertThat(cargo.itinerary().loadTimeAt(NEWYORK), is(oldDepartureTime));

    // Now voyage2 is rescheduled, the departure from NYC is delayed a few hours.
    Date newDepartureTime = toDate("2008-10-24", "18:00");
    voyage2.departureRescheduled(NEWYORK, newDepartureTime);

    // The schedule of voyage2 is updated
    assertThat(voyage2.schedule().departureTimeAt(NEWYORK), is(newDepartureTime));
    // ...but the cargo itinerary still has the old departure time
    assertThat(cargo.itinerary().loadTimeAt(NEWYORK), is(oldDepartureTime));

    // Generate a new itinerary from the old one and assign the cargo to this route
    Itinerary newItinerary = cargo.itinerary().withRescheduledVoyage(voyage2);
    cargo.assignToRoute(newItinerary);

    // Now the cargo aggregate is updated to reflect the scheduling change!
    assertThat(cargo.itinerary().loadTimeAt(NEWYORK), is(newDepartureTime));
    assertThat(cargo.routingStatus(), is(ROUTED));
  }

  @Test
  public void voyageIsRescheduledWithUnmaintainableRoute() {
    assertThat(cargo.routingStatus(), is(ROUTED));

    // Voyage1 arrives in NYC at 2008-10-23 23:10
    // Now rescheduling the departure of voyage2 to BEFORE
    // voyage1 arrives in NYC. This makes it impossible to
    // keep the latter part of the old itinerary, and the new itinerary
    // is therefore truncated after unload in NYC.

    // TODO delay the arrival instead of advancing the departure

    Date newDepartureTime = toDate("2008-10-23", "18:30");
    voyage2.departureRescheduled(NEWYORK, newDepartureTime);

    // Only the part of the itinerary up to and including NYC is maintainable, the rest is truncated
    Itinerary truncatedItinerary = cargo.itinerary().withRescheduledVoyage(voyage2);
    assertThat(truncatedItinerary.lastLeg().unloadLocation(), is(NEWYORK));

    //Or... The Itinerary is created with an 'Illegal Connection' based on a coomparison of
    //each transfer with a Location.minimumAllowedConnectionTime(). Since Loation is an entity
    //we don't allow Itinerary to dynamically use the property directly because it is not immutable.
    
    // The cargo enters MISROUTED state
    cargo.assignToRoute(truncatedItinerary);
    assertThat(cargo.routingStatus(), is(MISROUTED));
  }

}
