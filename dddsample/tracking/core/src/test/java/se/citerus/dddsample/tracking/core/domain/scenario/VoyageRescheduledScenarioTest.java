package se.citerus.dddsample.tracking.core.domain.scenario;

import org.junit.Before;
import org.junit.Test;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.TrackingIdFactoryInMem;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.MISROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus.ROUTED;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.*;

public class VoyageRescheduledScenarioTest {

  Cargo cargo;
  Voyage voyage1;
  Voyage voyage2;
  Voyage voyage3;

  @Before
  public void setupCargo() {
    TrackingIdFactoryInMem trackingIdFactory = new TrackingIdFactoryInMem();

    // Creating new voyages to avoid rescheduling shared ones, breaking other tests
    voyage1 = new Voyage(new VoyageNumber("V1"), HONGKONG_TO_NEW_YORK.schedule());
    voyage2 = new Voyage(new VoyageNumber("V2"), NEW_YORK_TO_DALLAS.schedule());
    voyage3 = new Voyage(new VoyageNumber("V3"), DALLAS_TO_HELSINKI.schedule());

    TrackingId trackingId = trackingIdFactory.nextTrackingId();
    RouteSpecification routeSpecification = new RouteSpecification(HANGZOU, STOCKHOLM, toDate("2008-12-23"));

    cargo = new Cargo(trackingId, routeSpecification);
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
    Date newDepartureTime = toDate("2008-10-24", "17:00");
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
