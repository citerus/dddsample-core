package se.citerus.dddsample.application.ws;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.service.InMemTransactionManager;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.HandlingEventService;
import se.citerus.dddsample.domain.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.domain.service.UnknownLocationException;
import se.citerus.dddsample.domain.service.UnknownTrackingIdException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlinEventServiceEndpointTest extends TestCase {

  private HandlingEventServiceEndpointImpl endpoint;
  private HandlingEventService handlingEventService;
  private SimpleDateFormat sdf;
  private HandlingEvent event;
  private Date date = new Date(100);

  protected void setUp() throws Exception {
    sdf = new SimpleDateFormat(HandlingEventServiceEndpointImpl.ISO_8601_FORMAT);

    endpoint = new HandlingEventServiceEndpointImpl();

    handlingEventService = createMock(HandlingEventService.class);
    endpoint.setHandlingEventService(handlingEventService);

    endpoint.setTransactionManager(new InMemTransactionManager());

    Cargo cargo = new Cargo(new TrackingId("FOO"), HONGKONG, NEWYORK);
    CarrierMovement carrierMovement = new CarrierMovement(
      new CarrierMovementId("CAR_456"),
      HONGKONG, TOKYO, new Date(), new Date());

    event = new HandlingEvent(
      cargo, date, new Date(), HandlingEvent.Type.LOAD, HONGKONG, carrierMovement
    );

    HandlingEventFactory handlingEventFactory = new HandlingEventFactory() {
      public HandlingEvent createHandlingEvent(Date completionTime, TrackingId trackingId, CarrierMovementId carrierMovementId, UnLocode unlocode, HandlingEvent.Type type) throws UnknownTrackingIdException, UnknownCarrierMovementIdException, UnknownLocationException {
        return event;
      }
    };

    endpoint.setHandlingEventFactory(handlingEventFactory);
  }

  public void testRegisterValidEvent() throws Exception {
    handlingEventService.register(event);
    replay(handlingEventService);

    // Tested call
    endpoint.register(sdf.format(date), "FOO", "CAR_456", "CNHKG", "LOAD");
  }

  protected void tearDown() throws Exception {
    verify(handlingEventService);
  }
}
