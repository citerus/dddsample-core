package se.citerus.dddsample.domain;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import junit.framework.TestCase;

public class DeliveryHistoryTest extends TestCase {
  public void testAddEventUnique() throws Exception {
    DeliveryHistory dh = new DeliveryHistory();
    
    DateFormat f = new SimpleDateFormat("yyyy-MM-dd");

    dh.addEvent(new HandlingEvent(f.parse("2010-01-01"), HandlingEvent.Type.RECEIVE, null));
    
    // Expect exception to be thrown when unique events are added (e.g. same timestamp)
    try {
      dh.addEvent(new HandlingEvent(f.parse("2010-01-01"), HandlingEvent.Type.LOAD, null));
      assertFalse(true);
    } catch (RuntimeException e) {
      assertTrue(true);
    }
  }
}
