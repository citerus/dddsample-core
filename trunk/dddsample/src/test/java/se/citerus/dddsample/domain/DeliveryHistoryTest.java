package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeliveryHistoryTest extends TestCase {
  private Location ham = new Location(new UnLocode("DE", "HAM"), "Hamburg");

  public void testEvensOrderedByTimeOccured() throws Exception {
    DeliveryHistory dh = new DeliveryHistory();
    assertTrue(dh.eventsOrderedByCompletionTime().isEmpty());

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    final Location from = new Location(new UnLocode("FR", "OMX"), "From");
    final Location to = new Location(new UnLocode("TO", "XXX"), "To");
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), from, to);
    HandlingEvent he1 = new HandlingEvent(null, df.parse("2010-01-03"), new Date(), HandlingEvent.Type.RECEIVE, to);
    HandlingEvent he2 = new HandlingEvent(null, df.parse("2010-01-01"), new Date(), HandlingEvent.Type.LOAD, to, carrierMovement);
    HandlingEvent he3 = new HandlingEvent(null, df.parse("2010-01-04"), new Date(), HandlingEvent.Type.CLAIM, from);
    HandlingEvent he4 = new HandlingEvent(null, df.parse("2010-01-02"), new Date(), HandlingEvent.Type.UNLOAD, from, carrierMovement);
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

    assertEquals(StatusCode.notReceived, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(null, new Date(10), null, HandlingEvent.Type.RECEIVE, ham));
    assertEquals(StatusCode.inPort, deliveryHistory.status());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), ham, ham);
    deliveryHistory.addEvent(new HandlingEvent(null, new Date(20), null, HandlingEvent.Type.LOAD, ham, carrierMovement));
    assertEquals(StatusCode.onBoardCarrier, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(null, new Date(30), null, HandlingEvent.Type.UNLOAD, ham, carrierMovement));
    assertEquals(StatusCode.inPort, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(null, new Date(40), null, HandlingEvent.Type.CLAIM, ham));
    assertEquals(StatusCode.claimed, deliveryHistory.status());
  }

  public void testCurrentLocation() throws Exception {
    DeliveryHistory deliveryHistory = new DeliveryHistory();

    assertNull(deliveryHistory.currentLocation());

    deliveryHistory.addEvent(new HandlingEvent(null, new Date(10), null, HandlingEvent.Type.RECEIVE, ham));
    assertEquals(ham, deliveryHistory.currentLocation());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), ham, ham);
    deliveryHistory.addEvent(new HandlingEvent(null, new Date(20), null, HandlingEvent.Type.LOAD, ham, carrierMovement));
    assertNull(deliveryHistory.currentLocation());
  }

}
