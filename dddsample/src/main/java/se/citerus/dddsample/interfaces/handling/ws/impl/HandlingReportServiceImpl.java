package se.citerus.dddsample.interfaces.handling.ws.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.application.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import static se.citerus.dddsample.interfaces.handling.HandlingReportParser.*;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReport;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportErrors;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportService;

import javax.jws.WebService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This web service endpoint implementation performs basic validation and parsing
 * of incoming data, and in case of a valid registration attempt, sends an asynchronous message
 * with the informtion to the handling event registration system for proper registration.
 *  
 */
@WebService(endpointInterface = "se.citerus.dddsample.interfaces.handling.ws.HandlingReportService")
public class HandlingReportServiceImpl implements HandlingReportService {

  private ApplicationEvents applicationEvents;
  private final static Log logger = LogFactory.getLog(HandlingReportServiceImpl.class);

  @Override
  public void submitReport(HandlingReport handlingReport) throws HandlingReportErrors {
    final List<String> errors = new ArrayList<String>();

    final Date completionTime = parseCompletionTime(handlingReport, errors);
    final VoyageNumber voyageNumber = parseVoyageNumber(handlingReport.getVoyageNumber(), errors);
    final HandlingEvent.Type type = parseEventType(handlingReport.getType(), errors);
    final UnLocode unLocode = parseUnLocode(handlingReport.getUnLocode(), errors);

    for (String trackingIdStr : handlingReport.getTrackingIds()) {
      final TrackingId trackingId = parseTrackingId(trackingIdStr, errors);

      if (errors.isEmpty()) {
        final Date registrationTime = new Date();
        final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
          registrationTime, completionTime, trackingId, voyageNumber, type, unLocode
        );

        applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
      } else {
        logger.error("Parse error in handling report: " + errors);
        throw new HandlingReportErrors(errors);
      }
    }
  }

  public void setApplicationEvents(ApplicationEvents applicationEvents) {
    this.applicationEvents = applicationEvents;
  }

}
