package se.citerus.dddsample.domain;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrackingScenarioTest extends TestCase {
  private final Location stockholm = new Location(new UnLocode("SE","STO"), "Stockholm");
  private final Location hamburg = new Location(new UnLocode("DE","HAM"), "Hamburg");
  private final Location melbourne = new Location(new UnLocode("AU","MEL"), "Melbourne");
  private final Location hongkong = new Location(new UnLocode("CN","HKG"), "Hongkong");

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
            new CarrierMovementId("CAR_001"), stockholm, hamburg);

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_002"), hamburg, hongkong);
    DeliveryHistory dh = new DeliveryHistory();
    dh.addAllEvents(Arrays.asList(
            new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, stockholm, stockholmToHamburg),
            new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, hamburg, stockholmToHamburg),
            new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, hamburg, hamburgToHongKong),
            new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.UNLOAD, hongkong, hamburgToHongKong)
    ));
    return dh;
  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), stockholm, melbourne);

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
