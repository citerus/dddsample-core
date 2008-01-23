package se.citerus.dddsample.ws;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.service.HandlingEventService;
import se.citerus.dddsample.service.InvalidEventTypeException;
import se.citerus.dddsample.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.service.UnknownTrackingIdException;

import javax.jws.WebService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebService(endpointInterface = "se.citerus.dddsample.ws.HandlingEventServiceEndpoint")
public class HandlingEventServiceEndpointImpl implements HandlingEventServiceEndpoint {

  private HandlingEventService handlingEventService;
  private static final Log logger = LogFactory.getLog(HandlingEventServiceEndpointImpl.class);
  protected static final String ISO_8601_FORMAT = "yyyy-mm-dd HH:MM:SS.SSS";

  public void register(String completionTime, String trackingId, String carrierMovementId,
                       String unlocode, String eventType) {
    try {
      Date date = parseIso8601Date(completionTime);
      TrackingId tid = new TrackingId(trackingId);
      CarrierMovementId cid;
      if (carrierMovementId != null) {
        cid = new CarrierMovementId(carrierMovementId);
      } else {
        cid = null;
      }
      HandlingEvent.Type type = parseEventType(eventType);

      handlingEventService.register(date, tid, cid, unlocode, type);
    } catch (ParseException pe) {
      logger.error("Invalid date format: " + completionTime + ", must be on ISO 8601 format: " + ISO_8601_FORMAT);
    } catch (UnknownTrackingIdException utid) {
      handleRetry(utid);
    } catch (UnknownCarrierMovementIdException ucmi) {
      handleRetry(ucmi);
    } catch (InvalidEventTypeException iete) {
      logger.error(iete, iete);
    } catch (Exception e) {
      logger.error(e, e);
    }
    // TODO: possibly handle "Duplicate event" exceptions due to unique constraint violations
  }

  private HandlingEvent.Type parseEventType(String eventType) throws InvalidEventTypeException {
    try {
      return HandlingEvent.Type.valueOf(eventType);
    } catch(IllegalArgumentException e) {
      throw new InvalidEventTypeException(eventType);
    }
  }

  private void handleRetry(Exception e) {
    logger.info("Placing event in retry queue due to: " + e.getMessage());
    // TODO: actually place in a retry queue
  }

  private Date parseIso8601Date(String completionTime) throws ParseException {
    return new SimpleDateFormat(ISO_8601_FORMAT).parse(completionTime);
  }

  public void setHandlingEventService(HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }
}
