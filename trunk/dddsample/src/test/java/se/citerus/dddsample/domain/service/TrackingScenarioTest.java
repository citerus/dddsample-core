package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoTestHelper;
import se.citerus.dddsample.domain.model.cargo.Delivery;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.CM001;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.CM002;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.UNLOAD;
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

    Delivery delivery = cargo.deliveryHistory();

    List<HandlingEvent> handlingEvents = delivery.history();

    assertEquals(4, handlingEvents.size());
    final HandlingEvent event = delivery.lastEvent();

    assertEquals(UNLOAD, event.type());

//    assertFalse(cargo.atFinalDestiation());
//    assertEquals("CNHKG", cargo.currentLocation().unlocode());

  }

  private Cargo populateCargo() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);

    final List<HandlingEvent> handlingEventList = Arrays.asList(
      new HandlingEvent(cargo, getDate("2007-12-01"), new Date(), LOAD, STOCKHOLM, CM001),
      new HandlingEvent(cargo, getDate("2007-12-02"), new Date(), UNLOAD, HAMBURG, CM001),
      new HandlingEvent(cargo, getDate("2007-12-03"), new Date(), LOAD, HAMBURG, CM002),
      new HandlingEvent(cargo, getDate("2007-12-05"), new Date(), UNLOAD, HONGKONG, CM002)
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
