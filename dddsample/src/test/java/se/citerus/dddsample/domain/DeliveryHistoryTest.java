package se.citerus.dddsample.domain;

import junit.framework.TestCase;
import static se.citerus.dddsample.domain.SampleLocations.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DeliveryHistoryTest extends TestCase {

  private Cargo cargo = new Cargo(new TrackingId("XYZ"), HONGKONG, NEWYORK);

  public void testEvensOrderedByTimeOccured() throws Exception {
    DeliveryHistory dh = new DeliveryHistory();
    assertTrue(dh.eventsOrderedByCompletionTime().isEmpty());

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), HONGKONG, NEWYORK);
    HandlingEvent he1 = new HandlingEvent(cargo, df.parse("2010-01-03"), new Date(), HandlingEvent.Type.RECEIVE, NEWYORK, null);
    HandlingEvent he2 = new HandlingEvent(cargo, df.parse("2010-01-01"), new Date(), HandlingEvent.Type.LOAD, NEWYORK, carrierMovement);
    HandlingEvent he3 = new HandlingEvent(cargo, df.parse("2010-01-04"), new Date(), HandlingEvent.Type.CLAIM, HONGKONG, null);
    HandlingEvent he4 = new HandlingEvent(cargo, df.parse("2010-01-02"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, carrierMovement);
    dh.addAllEvents(Arrays.asList(he1, he2, he3, he4));

    List<HandlingEvent> orderEvents = dh.eventsOrderedByCompletionTime();
    assertEquals(4, orderEvents.size());
    assertSame(he2, orderEvents.get(0));
    assertSame(he4, orderEvents.get(1));
    assertSame(he1, orderEvents.get(2));
    assertSame(he3, orderEvents.get(3));
  }

  public void testCargoStatusFromLastHandlingEvent() {
    DeliveryHistory deliveryHistory = new DeliveryHistory();

    assertEquals(StatusCode.NOT_RECIEVED, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG, null));
    assertEquals(StatusCode.IN_PORT, deliveryHistory.status());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), HAMBURG, HAMBURG);
    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, carrierMovement));
    assertEquals(StatusCode.ONBOARD_CARRIER, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(30), new Date(31), HandlingEvent.Type.UNLOAD, HAMBURG, carrierMovement));
    assertEquals(StatusCode.IN_PORT, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(40), new Date(41), HandlingEvent.Type.CLAIM, HAMBURG, null));
    assertEquals(StatusCode.CLAIMED, deliveryHistory.status());
  }

  public void testCurrentLocation() throws Exception {
    DeliveryHistory deliveryHistory = new DeliveryHistory();

    assertNull(deliveryHistory.currentLocation());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, HAMBURG, null));
    assertEquals(HAMBURG, deliveryHistory.currentLocation());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), HAMBURG, HAMBURG);
    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, HAMBURG, carrierMovement));
    assertNull(deliveryHistory.currentLocation());
  }

}
