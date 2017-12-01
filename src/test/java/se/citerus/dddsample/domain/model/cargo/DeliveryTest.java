package se.citerus.dddsample.domain.model.cargo;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.NEWYORK;

import java.util.Date;

import org.junit.Test;

public class DeliveryTest {

  private Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(HONGKONG, NEWYORK, new Date()));

  @Test
  public void testToSilenceWarnings() {
    assertThat(true).isTrue();
  }
  
  /*
  public void testEvensOrderedByTimeOccured() throws Exception {
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    HandlingEvent he1 = new HandlingEvent(cargo, df.parse("2010-01-03"), new Date(), HandlingEvent.Type.RECEIVE, NEWYORK);
    HandlingEvent he2 = new HandlingEvent(cargo, df.parse("2010-01-01"), new Date(), HandlingEvent.Type.LOAD, NEWYORK, CM003);
    HandlingEvent he3 = new HandlingEvent(cargo, df.parse("2010-01-04"), new Date(), HandlingEvent.Type.CLAIM, HONGKONG);
    HandlingEvent he4 = new HandlingEvent(cargo, df.parse("2010-01-02"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, CM004);
    Delivery dh = new Delivery(Arrays.asList(he1, he2, he3, he4));

    List<HandlingEvent> orderEvents = dh.history();
    assertThat(orderEvents).hasSize(4);
    assertThat(orderEvents).containsExactly(he2, he4, he1, he3);
  }

  public void testCargoStatusFromLastHandlingEvent() {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    Delivery delivery = new Delivery(events);

    assertThat(delivery.transportStatus()).isEqualTo(TransportStatus.NOT_RECEIVED);

    events.add(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG));
    delivery = new Delivery(events);
    assertThat(delivery.transportStatus()).isEqualTo(TransportStatus.IN_PORT);

    events.add(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, CM005));
    delivery = new Delivery(events);
    assertThat(delivery.transportStatus()).isEqualTo(TransportStatus.ONBOARD_CARRIER);

    events.add(new HandlingEvent(cargo, new Date(30), new Date(31), HandlingEvent.Type.UNLOAD, HAMBURG, CM006));
    delivery = new Delivery(events);
    assertThat(delivery.transportStatus()).isEqualTo(TransportStatus.IN_PORT);

    events.add(new HandlingEvent(cargo, new Date(40), new Date(41), HandlingEvent.Type.CLAIM, HAMBURG));
    delivery = new Delivery(events);
    assertThat(delivery.transportStatus()).isEqualTo(TransportStatus.CLAIMED);
  }

  public void testLastKnownLocation() throws Exception {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    Delivery delivery = new Delivery(events);

    assertThat(delivery.lastKnownLocation()).isEqualTo(Location.UNKNOWN);

    events.add(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG));
    delivery = new Delivery(events);

    assertThat(delivery.lastKnownLocation()).isEqualTo(HAMBURG);

    events.add(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, CM003));
    delivery = new Delivery(events);

    assertThat(delivery.lastKnownLocation()).isEqualTo(HAMBURG);
  }
  */
  
}
