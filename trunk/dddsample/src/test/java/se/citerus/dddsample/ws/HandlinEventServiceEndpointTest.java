package se.citerus.dddsample.ws;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.service.HandlingEventService;
import se.citerus.dddsample.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.service.UnknownTrackingIdException;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlinEventServiceEndpointTest extends TestCase {

  HandlingEventServiceEndpointImpl endpoint;
  HandlingEventService handlingEventService;
  SimpleDateFormat sdf;

  protected void setUp() throws Exception {
    endpoint = new HandlingEventServiceEndpointImpl();
    handlingEventService = createMock(HandlingEventService.class);
    endpoint.setHandlingEventService(handlingEventService);
    sdf = new SimpleDateFormat(HandlingEventServiceEndpointImpl.ISO_8601_FORMAT);
  }

  public void testRegisterValidEvent() throws Exception {
    Date date = new Date(100);

    handlingEventService.register(date, new TrackingId("FOO"), new CarrierMovementId("CAR_456"), new UnLocode("CN","HKG"), HandlingEvent.Type.LOAD);
    replay(handlingEventService);

    // Tested call
    endpoint.register(sdf.format(date), "FOO", "CAR_456", "CNHKG", "LOAD");
  }


  public void testRegisterUnknownTrackingId() throws Exception {
    Date date = new Date(100);

    TrackingId trackingId = new TrackingId("NOTFOUND");
    UnLocode unlocode = new UnLocode("SE","STO");

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

      handlingEventService.register(date, trackingId, carrierMovementId, new UnLocode("AU","MEL"), HandlingEvent.Type.UNLOAD);
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
