package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DeliveryHistoryTest extends TestCase {

  public void testEvensOrderedByTimeOccured() throws Exception {
    DeliveryHistory dh = new DeliveryHistory();
    assertTrue(dh.eventsOrderedByCompletionTime().isEmpty());

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    CarrierMovement carrierMovement = new CarrierMovement(new CarrierMovementId("CAR_001"), new Location("FROM"), new Location("TO"));
    HandlingEvent he1 = new HandlingEvent(null, df.parse("2010-01-03"), new Date(), HandlingEvent.Type.RECEIVE, new Location("TO"));
    HandlingEvent he2 = new HandlingEvent(null, df.parse("2010-01-01"), new Date(), HandlingEvent.Type.LOAD, new Location("TO"), carrierMovement);
    HandlingEvent he3 = new HandlingEvent(null, df.parse("2010-01-04"), new Date(), HandlingEvent.Type.CLAIM, new Location("FROM"));
    HandlingEvent he4 = new HandlingEvent(null, df.parse("2010-01-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("FROM"), carrierMovement);
    dh.addEvent(he1, he2, he3, he4);

    List<HandlingEvent> orderEvents = dh.eventsOrderedByCompletionTime();
    assertEquals(4, orderEvents.size());
    assertSame(he2, orderEvents.get(0));
    assertSame(he4, orderEvents.get(1));
    assertSame(he1, orderEvents.get(2));
    assertSame(he3, orderEvents.get(3));
  }
}
