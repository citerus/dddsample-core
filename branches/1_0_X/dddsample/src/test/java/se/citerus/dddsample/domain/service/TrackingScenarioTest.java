package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoTestHelper;
import se.citerus.dddsample.domain.model.cargo.DeliveryHistory;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TrackingScenarioTest extends TestCase {

  public void testTrackingScenarioStage1() throws Exception {

    Cargo cargo = populateCargo();

    DeliveryHistory deliveryHistory = cargo.deliveryHistory();

    List<HandlingEvent> handlingEvents = deliveryHistory.eventsOrderedByCompletionTime();

    assertEquals(4, handlingEvents.size());
    final HandlingEvent event = deliveryHistory.lastEvent();

    assertSame(HandlingEvent.Type.UNLOAD, event.type());
//    assertFalse(cargo.atFinalDestiation());
//    assertEquals("CNHKG", cargo.currentLocation().unlocode());

  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);
    final CarrierMovement stockholmToHamburg = new CarrierMovement(
            new CarrierMovementId("CAR_001"), STOCKHOLM, HAMBURG);

    final CarrierMovement hamburgToHongKong = new CarrierMovement(
            new CarrierMovementId("CAR_002"), HAMBURG, HONGKONG);
    final List<HandlingEvent> handlingEventList = Arrays.asList(
      new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), HandlingEvent.Type.LOAD, STOCKHOLM, stockholmToHamburg),
      new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), HandlingEvent.Type.UNLOAD, HAMBURG, stockholmToHamburg),
      new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), HandlingEvent.Type.LOAD, HAMBURG, hamburgToHongKong),
      new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), HandlingEvent.Type.UNLOAD, HONGKONG, hamburgToHongKong)
    );
    CargoTestHelper.setDeliveryHistory(cargo, handlingEventList);

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
