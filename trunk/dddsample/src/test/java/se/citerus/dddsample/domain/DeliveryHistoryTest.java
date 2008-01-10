package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class DeliveryHistoryTest extends TestCase {

  public void testEvensOrderedByTime() throws Exception {
    DeliveryHistory dh = new DeliveryHistory();
    assertTrue(dh.eventsOrderedByTime().isEmpty());

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    HandlingEvent he1 = new HandlingEvent(df.parse("2010-01-03"), HandlingEvent.Type.RECEIVE);
    HandlingEvent he2 = new HandlingEvent(df.parse("2010-01-01"), HandlingEvent.Type.LOAD);
    HandlingEvent he3 = new HandlingEvent(df.parse("2010-01-04"), HandlingEvent.Type.CLAIM);
    HandlingEvent he4 = new HandlingEvent(df.parse("2010-01-02"), HandlingEvent.Type.UNLOAD);
    dh.addEvent(he1, he2, he3, he4);

    List<HandlingEvent> orderEvents = dh.eventsOrderedByTime();
    assertEquals(4, orderEvents.size());
    assertSame(he2, orderEvents.get(0));
    assertSame(he4, orderEvents.get(1));
    assertSame(he1, orderEvents.get(2));
    assertSame(he3, orderEvents.get(3));
  }
}
