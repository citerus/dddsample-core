package se.citerus.dddsample.interfaces.handling;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.application.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.application.SystemEvents;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportErrors;
import se.citerus.dddsample.interfaces.handling.ws.impl.HandlingReportServiceImpl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class RegistrationParser {

  private SystemEvents systemEvents;
  
  private static final Log logger = LogFactory.getLog(RegistrationParser.class);

  public void convertAndSend(String completionTime, String trackingId, String voyageNumberString, String unlocode, String eventType) throws HandlingReportErrors {
    final List<String> errors = new ArrayList<String>();

    final Date date = parseDate(completionTime, errors);
    final TrackingId tid = parseTrackingId(trackingId, errors);
    final VoyageNumber voyageNumber = parseVoyageNumber(voyageNumberString, errors);
    final HandlingEvent.Type type = parseEventType(eventType, errors);
    final UnLocode ul = parseUnLocode(unlocode, errors);

    if (errors.isEmpty()) {
      final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(new Date(), date, tid, voyageNumber, type, ul);
      systemEvents.receivedHandlingEventRegistrationAttempt(attempt);
    } else {
      logger.warn("Handling event registration attempt failed: " + errors);
      throw new HandlingReportErrors(errors);
    }
  }

  private UnLocode parseUnLocode(final String unlocode, final List<String> errors) {
    try {
      return new UnLocode(unlocode);
    } catch (IllegalArgumentException e) {
      errors.add(e.getMessage());
      return null;
    }
  }

  private TrackingId parseTrackingId(final String trackingId, final List<String> errors) {
    try {
      return new TrackingId(trackingId);
    } catch (IllegalArgumentException e) {
      errors.add(e.getMessage());
      return null;
    }
  }

  private VoyageNumber parseVoyageNumber(final String voyageNumber, final List<String> errors) {
    if (StringUtils.isNotEmpty(voyageNumber)) {
      try {
        return new VoyageNumber(voyageNumber);
      } catch (IllegalArgumentException e) {
        errors.add(e.getMessage());
        return null;
      }
    } else {
      return null;
    }
  }

  private Date parseDate(final String completionTime, final List<String> errors) {
    Date date;
    try {
      date = new SimpleDateFormat(HandlingReportServiceImpl.ISO_8601_FORMAT).parse(completionTime);
    } catch (ParseException e) {
      errors.add("Invalid date format: " + completionTime + ", must be on ISO 8601 format: " + HandlingReportServiceImpl.ISO_8601_FORMAT);
      date = null;
    }
    return date;
  }

  private HandlingEvent.Type parseEventType(final String eventType, final List<String> errors) {
    try {
      return HandlingEvent.Type.valueOf(eventType);
    } catch (IllegalArgumentException e) {
      errors.add(eventType + " is not a valid handling event type. Valid types are: " + Arrays.toString(HandlingEvent.Type.values()));
      return null;
    }
  }

  public void setSystemEvents(SystemEvents systemEvents) {
    this.systemEvents = systemEvents;
  }
}
