package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class DeliveryHistoryTest extends TestCase {
  private Location ham = new Location(new UnLocode("DE", "HAM"), "Hamburg");
  private Location from = new Location(new UnLocode("FR", "OMX"), "From");
  private Location to = new Location(new UnLocode("TO", "XXX"), "To");
  private Cargo cargo = new Cargo(new TrackingId("XYZ"), from, to);


  public void testEvensOrderedByTimeOccured() throws Exception {
    DeliveryHistory dh = new DeliveryHistory();
    assertTrue(dh.eventsOrderedByCompletionTime().isEmpty());

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), from, to);
    HandlingEvent he1 = new HandlingEvent(cargo, df.parse("2010-01-03"), new Date(), HandlingEvent.Type.RECEIVE, to, null);
    HandlingEvent he2 = new HandlingEvent(cargo, df.parse("2010-01-01"), new Date(), HandlingEvent.Type.LOAD, to, carrierMovement);
    HandlingEvent he3 = new HandlingEvent(cargo, df.parse("2010-01-04"), new Date(), HandlingEvent.Type.CLAIM, from, null);
    HandlingEvent he4 = new HandlingEvent(cargo, df.parse("2010-01-02"), new Date(), HandlingEvent.Type.UNLOAD, from, carrierMovement);
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

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, ham, null));
    assertEquals(StatusCode.IN_PORT, deliveryHistory.status());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), ham, ham);
    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, ham, carrierMovement));
    assertEquals(StatusCode.ONBOARD_CARRIER, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(30), new Date(31), HandlingEvent.Type.UNLOAD, ham, carrierMovement));
    assertEquals(StatusCode.IN_PORT, deliveryHistory.status());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(40), new Date(41), HandlingEvent.Type.CLAIM, ham, null));
    assertEquals(StatusCode.CLAIMED, deliveryHistory.status());
  }

  public void testCurrentLocation() throws Exception {
    DeliveryHistory deliveryHistory = new DeliveryHistory();

    assertNull(deliveryHistory.currentLocation());

    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(10), new Date(11), HandlingEvent.Type.RECEIVE, ham, null));
    assertEquals(ham, deliveryHistory.currentLocation());

    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("ABC"), ham, ham);
    deliveryHistory.addEvent(new HandlingEvent(cargo, new Date(20), new Date(21), HandlingEvent.Type.LOAD, ham, carrierMovement));
    assertNull(deliveryHistory.currentLocation());
  }

}
