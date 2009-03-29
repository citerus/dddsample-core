package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.NEWYORK;

import java.util.Date;

public class DeliveryTest extends TestCase {

  private Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(HONGKONG, NEWYORK, new Date()));

  public void testToSilenceWarnings() throws Exception {
    assertTrue(true);
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
    assertEquals(4, orderEvents.size());
    assertSame(he2, orderEvents.get(0));
    assertSame(he4, orderEvents.get(1));
    assertSame(he1, orderEvents.get(2));
    assertSame(he3, orderEvents.get(3));
  }

  public void testCargoStatusFromLastHandlingEvent() {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    Delivery delivery = new Delivery(events);

    assertEquals(TransportStatus.NOT_RECEIVED, delivery.transportStatus());

    events.add(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG));
    delivery = new Delivery(events);
    assertEquals(TransportStatus.IN_PORT, delivery.transportStatus());

    events.add(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, CM005));
    delivery = new Delivery(events);
    assertEquals(TransportStatus.ONBOARD_CARRIER, delivery.transportStatus());

    events.add(new HandlingEvent(cargo, new Date(30), new Date(31), HandlingEvent.Type.UNLOAD, HAMBURG, CM006));
    delivery = new Delivery(events);
    assertEquals(TransportStatus.IN_PORT, delivery.transportStatus());

    events.add(new HandlingEvent(cargo, new Date(40), new Date(41), HandlingEvent.Type.CLAIM, HAMBURG));
    delivery = new Delivery(events);
    assertEquals(TransportStatus.CLAIMED, delivery.transportStatus());
  }

  public void testLastKnownLocation() throws Exception {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    Delivery delivery = new Delivery(events);

    assertEquals(Location.UNKNOWN, delivery.lastKnownLocation());

    events.add(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG));
    delivery = new Delivery(events);

    assertEquals(HAMBURG, delivery.lastKnownLocation());

    events.add(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, CM003));
    delivery = new Delivery(events);

    assertEquals(HAMBURG, delivery.lastKnownLocation());
  }
  */
  
}
