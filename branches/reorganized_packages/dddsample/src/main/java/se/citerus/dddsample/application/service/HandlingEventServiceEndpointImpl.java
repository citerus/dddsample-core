package se.citerus.dddsample.application.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.TrackingId;
import se.citerus.dddsample.domain.model.*;
import se.citerus.dddsample.domain.model.UnLocode;
import se.citerus.dddsample.domain.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.domain.service.*;
import se.citerus.dddsample.domain.service.UnknownTrackingIdException;
import se.citerus.dddsample.application.service.api.HandlingEventServiceEndpoint;

import javax.jws.WebService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebService(endpointInterface = "se.citerus.dddsample.application.service.api.HandlingEventServiceEndpoint")
public class HandlingEventServiceEndpointImpl implements HandlingEventServiceEndpoint {

  private HandlingEventService handlingEventService;
  private final Log logger = LogFactory.getLog(getClass());
  protected static final String ISO_8601_FORMAT = "yyyy-mm-dd HH:MM:SS.SSS";

  public void register(final String completionTime, final String trackingId, final String carrierMovementId,
                       final String unlocode, final String eventType) {
    try {
      Date date = parseIso8601Date(completionTime);
      TrackingId tid = new TrackingId(trackingId);
      CarrierMovementId cid;
      if (StringUtils.isNotBlank(carrierMovementId)) {
        cid = new CarrierMovementId(carrierMovementId);
      } else {
        cid = null;
      }
      HandlingEvent.Type type = parseEventType(eventType);

      UnLocode ul = new UnLocode(unlocode);

      handlingEventService.register(date, tid, cid, ul, type);
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

  private HandlingEvent.Type parseEventType(final String eventType) throws InvalidEventTypeException {
    try {
      return HandlingEvent.Type.valueOf(eventType);
    } catch (IllegalArgumentException e) {
      throw new InvalidEventTypeException(eventType);
    }
  }

  private void handleRetry(Exception e) {
    logger.info("Placing event in retry queue due to: " + e.getMessage());
    // TODO: actually place in a retry queue
  }

  private Date parseIso8601Date(final String completionTime) throws ParseException {
    return new SimpleDateFormat(ISO_8601_FORMAT).parse(completionTime);
  }

  public void setHandlingEventService(final HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }
}
