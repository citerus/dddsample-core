package se.citerus.dddsample.tracking.core.interfaces.tracking;

import junit.framework.TestCase;
import org.springframework.context.support.StaticApplicationContext;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HANGZOU;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HELSINKI;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.CM001;

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

    cargo.handled(new HandlingActivity(HandlingEvent.Type.RECEIVE, HANGZOU));
    cargo.handled(new HandlingActivity(HandlingEvent.Type.LOAD, HANGZOU, CM001));
    cargo.handled(new HandlingActivity(HandlingEvent.Type.UNLOAD, HELSINKI, CM001));

    StaticApplicationContext applicationContext = new StaticApplicationContext();
    applicationContext.addMessage("cargo.status.IN_PORT", Locale.GERMAN, "In port {0}");
    applicationContext.refresh();

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
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
