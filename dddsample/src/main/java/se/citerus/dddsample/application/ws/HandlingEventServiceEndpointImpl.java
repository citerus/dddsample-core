package se.citerus.dddsample.application.ws;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.HandlingEventService;
import se.citerus.dddsample.domain.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.domain.service.UnknownTrackingIdException;

import javax.jws.WebService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebService(endpointInterface = "se.citerus.dddsample.application.ws.HandlingEventServiceEndpoint")
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
      if (StringUtils.isNotEmpty(carrierMovementId)) {
        cid = new CarrierMovementId(carrierMovementId);
      } else {
        cid = null;
      }
      HandlingEvent.Type type = parseEventType(eventType);

      UnLocode ul = new UnLocode(unlocode);

      handlingEventService.register(date, tid, cid, ul, type);

    } catch (IllegalArgumentException iae) {
      handleIllegalArgument(iae);

    } catch (ParseException pe) {
      handleInvalidDateFormat(completionTime);

    } catch (UnknownTrackingIdException utid) {
      handleUnknownTrackingId(utid);

    } catch (UnknownCarrierMovementIdException ucmi) {
      handleUnknownCarrierMovementId(ucmi);

    } catch (InvalidEventTypeException iete) {
      handleInvalidEventType(iete);

    } catch (Exception e) {
      handleOtherError(e);
    }
  }

  private void handleIllegalArgument(IllegalArgumentException iae) {
    logger.error(iae, iae);
  }

  private void handleOtherError(Exception e) {
    logger.error(e, e);
  }

  private void handleInvalidEventType(InvalidEventTypeException iete) {
    logger.error(iete, iete);
  }

  private void handleInvalidDateFormat(String completionTime) {
    logger.error("Invalid date format: " + completionTime + ", must be on ISO 8601 format: " + ISO_8601_FORMAT);
  }

  private HandlingEvent.Type parseEventType(final String eventType) throws InvalidEventTypeException {
    try {
      return HandlingEvent.Type.valueOf(eventType);
    } catch (IllegalArgumentException e) {
      throw new InvalidEventTypeException(eventType);
    }
  }

  private void handleUnknownCarrierMovementId(UnknownCarrierMovementIdException e) {
    logger.info("Placing event in retry queue due to: " + e.getMessage());
  }

  private void handleUnknownTrackingId(Exception e) {
    logger.info("Placing event in retry queue due to: " + e.getMessage());
  }

  private Date parseIso8601Date(final String completionTime) throws ParseException {
    return new SimpleDateFormat(ISO_8601_FORMAT).parse(completionTime);
  }

  public void setHandlingEventService(final HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }
}
