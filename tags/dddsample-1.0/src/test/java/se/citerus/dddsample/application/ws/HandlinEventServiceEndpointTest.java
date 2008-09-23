package se.citerus.dddsample.application.ws;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.service.InMemTransactionManager;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.HandlingEventService;
import se.citerus.dddsample.domain.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.domain.service.UnknownTrackingIdException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlinEventServiceEndpointTest extends TestCase {

  private HandlingEventServiceEndpointImpl endpoint;
  private HandlingEventService handlingEventService;
  private SimpleDateFormat sdf;

  protected void setUp() throws Exception {
    endpoint = new HandlingEventServiceEndpointImpl();
    handlingEventService = createMock(HandlingEventService.class);
    endpoint.setHandlingEventService(handlingEventService);
    sdf = new SimpleDateFormat(HandlingEventServiceEndpointImpl.ISO_8601_FORMAT);
    endpoint.setTransactionManager(new InMemTransactionManager());
  }

  public void testRegisterValidEvent() throws Exception {
    Date date = new Date(100);

    handlingEventService.register(date, new TrackingId("FOO"), new CarrierMovementId("CAR_456"), new UnLocode("CNHKG"), HandlingEvent.Type.LOAD);
    replay(handlingEventService);

    // Tested call
    endpoint.register(sdf.format(date), "FOO", "CAR_456", "CNHKG", "LOAD");
  }


  public void testRegisterUnknownTrackingId() throws Exception {
    Date date = new Date(100);

    TrackingId trackingId = new TrackingId("NOTFOUND");
    UnLocode unlocode = new UnLocode("SESTO");

    handlingEventService.register(date, trackingId, null, unlocode, HandlingEvent.Type.CLAIM);
    expectLastCall().andThrow(new UnknownTrackingIdException(trackingId));
    replay(handlingEventService);

    // Tested call
    endpoint.register(sdf.format(date), "NOTFOUND", null, "SESTO", "CLAIM");
  }

  public void testRegisterUnknownCarrierMovementId() throws Exception {
    Date date = new Date(100);

    TrackingId trackingId = new TrackingId("XYZ");
    CarrierMovementId carrierMovementId = new CarrierMovementId("NOTFOUND");

    handlingEventService.register(date, trackingId, carrierMovementId, new UnLocode("AUMEL"), HandlingEvent.Type.UNLOAD);
    expectLastCall().andThrow(new UnknownCarrierMovementIdException(carrierMovementId));
    replay(handlingEventService);

    // Tested call
    endpoint.register(sdf.format(date), "XYZ", "NOTFOUND", "AUMEL", "UNLOAD");
  }

  public void testRegisterInvalidEventType() throws Exception {
    Date date = new Date(100);

    replay(handlingEventService);

    // Tested call
    // Note: currently, every error is silently swallowed.
    endpoint.register(sdf.format(date), "XYZ", "CAR_333", "AUMEL", "NO_SUCH_EVENT_TYPE");
  }

  public void testRegisterInvalidDateFormat() throws Exception {
    Date date = new Date(100);

    replay(handlingEventService);

    // Tested call
    // Note: currently, every error is silently swallowed.
    endpoint.register("1 2 3 4", "XYZ", "CAR_333", "AUMEL", "LOAD");
  }


  protected void tearDown() throws Exception {
    verify(handlingEventService);
  }
}
