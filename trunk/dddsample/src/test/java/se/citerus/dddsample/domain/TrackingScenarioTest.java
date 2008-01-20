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

    DeliveryHistory deliveryHistory = populateDeliveryHistory(cargo);

    List<HandlingEvent> handlingEvents = deliveryHistory.eventsOrderedByCompletionTime();

    assertEquals(4, handlingEvents.size());
    final HandlingEvent event = deliveryHistory.lastEvent();

    assertSame(HandlingEvent.Type.UNLOAD, event.type());
//    assertFalse(cargo.atFinalDestiation());
//    assertEquals("CNHKG", cargo.currentLocation().unlocode());

  }

  private DeliveryHistory populateDeliveryHistory(Cargo cargo) throws Exception {
    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("DEHAM"));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_002"), new Location("DEHAM"), new Location("CNHKG"));
    DeliveryHistory dh = new DeliveryHistory();
    dh.addEvent(
            new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, new Location("SESTO"), stockholmToHamburg),
            new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, new Location("DEHAM"), stockholmToHamburg),
            new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, new Location("DEHAM"), hamburgToHongKong),
            new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.UNLOAD, new Location("CNHKG"), hamburgToHongKong)
    );
    return dh;
  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));

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
