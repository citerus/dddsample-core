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

    DeliveryHistory deliveryHistory = cargo.getDeliveryHistory();

    List<HandlingEvent> handlingEvents = deliveryHistory.eventsOrderedByTime();

    assertEquals(4, handlingEvents.size());
    final HandlingEvent event = deliveryHistory.lastEvent();

    assertSame(HandlingEvent.Type.UNLOAD, event.getType());
    assertFalse(cargo.atFinalDestiation());
    assertEquals("CNHKG", cargo.getCurrentLocation().unlocode());

  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    cargo.handle(new HandlingEvent(getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholmToHamburg));
    cargo.handle(new HandlingEvent(getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierId("CAR_002"), new Location("DEHAM"), new Location("CNHKG"));

    cargo.handle(new HandlingEvent(getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburgToHongKong));
    cargo.handle(new HandlingEvent(getDate("2007-12-05"), new Date(), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

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
