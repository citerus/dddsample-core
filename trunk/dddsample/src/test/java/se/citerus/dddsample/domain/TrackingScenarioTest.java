package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrackingScenarioTest extends TestCase {

  public void testTrackingScenarioStage1() throws Exception {

    Cargo cargo = populateCargo();

    final List<HandlingEvent> handlingEvents = cargo.eventsOrderedByTime();
    final HandlingEvent event = cargo.lastEvent();

//
//    DeliveryHistory deliveryHistory = cargo.deliveryHistory();
//
//    List<HandlingEvent> handlingEvents = deliveryHistory.eventsOrderedByTime();
//
    assertEquals(4, handlingEvents.size());
    
    assertSame(HandlingEvent.Type.UNLOAD, event.type());
    assertFalse(cargo.atFinalDestiation());
    assertEquals("CNHKG", cargo.currentLocation().unlocode());

  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_002"), new Location("DEHAM"), new Location("CNHKG"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("2007-12-05"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHKG"), hamburgToHongKong));

    return cargo;
  }

  /**
   * Parse an ISO 8601 (YYYY-MM-DD) String to Date
   *
   * @param isoFormat String to parse.
   * @return Created date instance.
   * @throws ParseException Thrown if parsing fails.
   */
  private Date getDate(String isoFormat) throws ParseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.parse(isoFormat);
  }

}
