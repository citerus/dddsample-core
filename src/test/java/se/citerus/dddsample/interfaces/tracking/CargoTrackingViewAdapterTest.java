package se.citerus.dddsample.interfaces.tracking;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HANGZOU;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;

public class CargoTrackingViewAdapterTest {

  @Test
  public void testCreate() {
    Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(HANGZOU, HELSINKI, new Date()));

    List<HandlingEvent> events = new ArrayList<HandlingEvent>();
    events.add(new HandlingEvent(cargo, new Date(1), new Date(2), HandlingEvent.Type.RECEIVE, HANGZOU));

    events.add(new HandlingEvent(cargo, new Date(3), new Date(4), HandlingEvent.Type.LOAD, HANGZOU, CM001));
    events.add(new HandlingEvent(cargo, new Date(5), new Date(6), HandlingEvent.Type.UNLOAD, HELSINKI, CM001));

    cargo.deriveDeliveryProgress(new HandlingHistory(events));

    StaticApplicationContext applicationContext = new StaticApplicationContext();
    applicationContext.addMessage("cargo.status.IN_PORT", Locale.GERMAN, "In port {0}");
    applicationContext.refresh();

    CargoTrackingViewAdapter adapter = new CargoTrackingViewAdapter(cargo, applicationContext, Locale.GERMAN, events, TimeZone.getTimeZone("Europe/Stockholm"));

    assertThat(adapter.getTrackingId()).isEqualTo("XYZ");
    assertThat(adapter.getOrigin()).isEqualTo("Hangzhou");
    assertThat(adapter.getDestination()).isEqualTo("Helsinki");
    assertThat(adapter.getStatusText()).isEqualTo("In port Helsinki");

    Iterator<CargoTrackingViewAdapter.HandlingEventViewAdapter> it = adapter.getEvents().iterator();

    CargoTrackingViewAdapter.HandlingEventViewAdapter event = it.next();
    assertThat(event.getType()).isEqualTo("RECEIVE");
    assertThat(event.getLocation()).isEqualTo("Hangzhou");
    assertThat(event.getTime()).isEqualTo("1970-01-01 01:00");
    assertThat(event.getVoyageNumber()).isEqualTo("");
    assertThat(event.isExpected()).isTrue();

    event = it.next();
    assertThat(event.getType()).isEqualTo("LOAD");
    assertThat(event.getLocation()).isEqualTo("Hangzhou");
    assertThat(event.getTime()).isEqualTo("1970-01-01 01:00");
    assertThat(event.getVoyageNumber()).isEqualTo("CM001");
    assertThat(event.isExpected()).isTrue();

    event = it.next();
    assertThat(event.getType()).isEqualTo("UNLOAD");
    assertThat(event.getLocation()).isEqualTo("Helsinki");
    assertThat(event.getTime()).isEqualTo("1970-01-01 01:00");
    assertThat(event.getVoyageNumber()).isEqualTo("CM001");
    assertThat(event.isExpected()).isTrue();
  }

}
