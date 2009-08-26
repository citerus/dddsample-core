package se.citerus.dddsample.interfaces.tracking;

import junit.framework.TestCase;
import org.springframework.context.support.StaticApplicationContext;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HANGZOU;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

import java.util.*;

public class CargoTrackingViewAdapterTest extends TestCase {

  public void testCreate() {
    // Disable test for now, CargoTrackingViewAdapter is being reconsidered 
    if (true) return;

    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(HANGZOU, HELSINKI, new Date()));
//	TODO: Need to put an itinerary on the Cargo in order to test the
//	isExpected(). Those assertions are commented out because they only
//	worked as a side-effect of the incorrect (now corrected) behavior of 
//  Itinerary, which previously said than an empty itinerary considered all
//  events as "expected", whereas an empty itinerary should actually consider
//  any event unexpected. (ie empty itinerary means nothing happens.)

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, new Date(1), new Date(2), HandlingEvent.Type.RECEIVE, HANGZOU));

    events.add(new HandlingEvent(cargo, new Date(3), new Date(4), HandlingEvent.Type.LOAD, HANGZOU, CM001));
    events.add(new HandlingEvent(cargo, new Date(5), new Date(6), HandlingEvent.Type.UNLOAD, HELSINKI, CM001));

    cargo.deriveDeliveryProgress(HandlingHistory.fromEvents(events));

    StaticApplicationContext applicationContext = new StaticApplicationContext();
    applicationContext.addMessage("cargo.status.IN_PORT", Locale.GERMAN, "In port {0}");
    applicationContext.refresh();

    CargoTrackingViewAdapter adapter = new CargoTrackingViewAdapter(cargo, applicationContext, Locale.GERMAN, events);

    assertEquals("XYZ", adapter.getTrackingId());
    assertEquals("Hangzhou", adapter.getOrigin());
    assertEquals("Helsinki", adapter.getDestination());
    assertEquals("In port Helsinki", adapter.getStatusText());

    Iterator<CargoTrackingViewAdapter.HandlingEventViewAdapter> it = adapter.getEvents().iterator();

    CargoTrackingViewAdapter.HandlingEventViewAdapter event = it.next();
    assertEquals("RECEIVE", event.getType());
    assertEquals("Hangzhou", event.getLocation());
    assertEquals("1970-01-01 08:00 CST", event.getTime());
    assertEquals("", event.getVoyageNumber());
//    assertTrue(event.isExpected());

    event = it.next();
    assertEquals("LOAD", event.getType());
    assertEquals("Hangzhou", event.getLocation());
    assertEquals("1970-01-01 08:00 CST", event.getTime());
    assertEquals("CM001", event.getVoyageNumber());
//    assertTrue(event.isExpected());

    event = it.next();
    assertEquals("UNLOAD", event.getType());
    assertEquals("Helsinki", event.getLocation());
    assertEquals("1970-01-01 01:00 CET", event.getTime());
    assertEquals("CM001", event.getVoyageNumber());
//    assertTrue(event.isExpected());
  }

}
