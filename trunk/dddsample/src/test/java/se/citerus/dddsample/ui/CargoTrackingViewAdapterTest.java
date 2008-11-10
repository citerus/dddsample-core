package se.citerus.dddsample.ui;

import junit.framework.TestCase;
import org.springframework.context.support.StaticApplicationContext;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoTestHelper;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.CM001;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HANGZOU;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;

import java.util.*;

public class CargoTrackingViewAdapterTest extends TestCase {

  public void testCreate() {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), HANGZOU, HELSINKI);

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, new Date(1), new Date(2), HandlingEvent.Type.RECEIVE, HANGZOU));

    events.add(new HandlingEvent(cargo, new Date(3), new Date(4), HandlingEvent.Type.LOAD, HANGZOU, CM001));
    events.add(new HandlingEvent(cargo, new Date(5), new Date(6), HandlingEvent.Type.UNLOAD, HELSINKI, CM001));

    CargoTestHelper.setDeliveryHistory(cargo, events);

    StaticApplicationContext applicationContext = new StaticApplicationContext();
    applicationContext.addMessage("cargo.status.IN_PORT", Locale.GERMAN, "In port {0}");
    applicationContext.refresh();

    CargoTrackingViewAdapter adapter = new CargoTrackingViewAdapter(cargo, applicationContext, Locale.GERMAN);

    assertEquals("XYZ", adapter.getTrackingId());
    assertEquals("CNHGH (Hangzhou)", adapter.getOrigin());
    assertEquals("FIHEL (Helsinki)", adapter.getDestination());
    assertEquals("In port FIHEL (Helsinki)", adapter.getStatusText());

    Iterator<CargoTrackingViewAdapter.HandlingEventViewAdapter> it = adapter.getEvents().iterator();

    CargoTrackingViewAdapter.HandlingEventViewAdapter event = it.next();
    assertEquals("RECEIVE", event.getType());
    assertEquals("CNHGH", event.getLocation());
    assertEquals("1970-01-01 01:00", event.getTime());
    assertEquals("", event.getVoyageNumber());
    assertTrue(event.isExpected());

    event = it.next();
    assertEquals("LOAD", event.getType());
    assertEquals("CNHGH", event.getLocation());
    assertEquals("1970-01-01 01:00", event.getTime());
    assertEquals("CM001", event.getVoyageNumber());
    assertTrue(event.isExpected());

    event = it.next();
    assertEquals("UNLOAD", event.getType());
    assertEquals("FIHEL", event.getLocation());
    assertEquals("1970-01-01 01:00", event.getTime());
    assertEquals("CM001", event.getVoyageNumber());
    assertTrue(event.isExpected());
  }

}
