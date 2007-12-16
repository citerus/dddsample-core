package se.citerus.dddsample.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.SortedSet;

import junit.framework.TestCase;

public class TrackingScenarioTest extends TestCase {

  public void testTrackingScenarioStage1() throws Exception {

    Cargo cargo = populateCargo();

    DeliveryHistory deliveryHistory = cargo.deliveryHistory();

    SortedSet<HandlingEvent> handlingEvents = deliveryHistory.events();

    assertEquals(4, handlingEvents.size());
    final HandlingEvent event = handlingEvents.last();
    assertSame(HandlingEvent.Type.OFF, event.type());
    assertFalse(cargo.atFinalDestiation());
    assertEquals("CNHKG", cargo.currentLocation().unlocode());

  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg =
       new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

    
    cargo.handle(new HandlingEvent(getDate("01-Dec-07"), HandlingEvent.Type.ON, stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("02-Dec-07"), HandlingEvent.Type.OFF, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong =
       new CarrierMovement(new Location("DEHAM"), new Location("CNHKG"));

    cargo.handle(new HandlingEvent(getDate("03-Dec-07"), HandlingEvent.Type.ON, hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("05-Dec-07"), HandlingEvent.Type.OFF, hamburgToHongKong));

    return cargo;
  }

	private Date getDate(String date) throws ParseException {
		return DateFormat.getDateInstance(DateFormat.DEFAULT).parse(date);
	}

}
