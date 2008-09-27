package se.citerus.dddsample.domain.model.cargo;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeliveryHistoryTest extends TestCase {

  private Cargo cargo = new Cargo(new TrackingId("XYZ"), HONGKONG, NEWYORK);

  public void testEvensOrderedByTimeOccured() throws Exception {

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), HONGKONG, NEWYORK, new Date(), new Date());
    HandlingEvent he1 = new HandlingEvent(cargo, df.parse("2010-01-03"), new Date(), HandlingEvent.Type.RECEIVE, NEWYORK);
    HandlingEvent he2 = new HandlingEvent(cargo, df.parse("2010-01-01"), new Date(), HandlingEvent.Type.LOAD, NEWYORK, carrierMovement);
    HandlingEvent he3 = new HandlingEvent(cargo, df.parse("2010-01-04"), new Date(), HandlingEvent.Type.CLAIM, HONGKONG);
    HandlingEvent he4 = new HandlingEvent(cargo, df.parse("2010-01-02"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, carrierMovement);
    DeliveryHistory dh = new DeliveryHistory(Arrays.asList(he1, he2, he3, he4));

    List<HandlingEvent> orderEvents = dh.eventsOrderedByCompletionTime();
    assertEquals(4, orderEvents.size());
    assertSame(he2, orderEvents.get(0));
    assertSame(he4, orderEvents.get(1));
    assertSame(he1, orderEvents.get(2));
    assertSame(he3, orderEvents.get(3));
  }

  public void testCargoStatusFromLastHandlingEvent() {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    DeliveryHistory deliveryHistory = new DeliveryHistory(events);

    assertEquals(StatusCode.NOT_RECEIVED, deliveryHistory.status());

    events.add(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG));
    deliveryHistory = new DeliveryHistory(events);
    assertEquals(StatusCode.IN_PORT, deliveryHistory.status());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), HAMBURG, HAMBURG, new Date(), new Date());
    events.add(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, carrierMovement));
    deliveryHistory = new DeliveryHistory(events);
    assertEquals(StatusCode.ONBOARD_CARRIER, deliveryHistory.status());

    events.add(new HandlingEvent(cargo, new Date(30), new Date(31), HandlingEvent.Type.UNLOAD, HAMBURG, carrierMovement));
    deliveryHistory = new DeliveryHistory(events);
    assertEquals(StatusCode.IN_PORT, deliveryHistory.status());

    events.add(new HandlingEvent(cargo, new Date(40), new Date(41), HandlingEvent.Type.CLAIM, HAMBURG));
    deliveryHistory = new DeliveryHistory(events);
    assertEquals(StatusCode.CLAIMED, deliveryHistory.status());
  }

  public void testCurrentLocation() throws Exception {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    DeliveryHistory deliveryHistory = new DeliveryHistory(events);

    assertEquals(Location.UNKNOWN, deliveryHistory.currentLocation());

    events.add(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG));
    deliveryHistory = new DeliveryHistory(events);

    assertEquals(HAMBURG, deliveryHistory.currentLocation());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), HAMBURG, HAMBURG, new Date(), new Date());
    events.add(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, carrierMovement));
    deliveryHistory = new DeliveryHistory(events);

    assertEquals(Location.UNKNOWN, deliveryHistory.currentLocation());
  }

}
