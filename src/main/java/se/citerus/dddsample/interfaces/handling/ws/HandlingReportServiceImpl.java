package se.citerus.dddsample.interfaces.handling.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.citerus.dddsample.interfaces.handling.HandlingReportParser.*;

/**
 * This web service endpoint implementation performs basic validation and parsing
 * of incoming data, and in case of a valid registration attempt, sends an asynchronous message
 * with the information to the handling event registration system for proper registration.
 */
@RestController
public class HandlingReportServiceImpl implements HandlingReportService {

  private final ApplicationEvents applicationEvents;
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  public HandlingReportServiceImpl(ApplicationEvents applicationEvents) {
    this.applicationEvents = applicationEvents;
  }

  @PostMapping(value = "/handlingReport", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<?> submitReport(@RequestBody HandlingReport handlingReport) {
    final List<String> errors = new ArrayList<>();

    final Date completionTime = parseCompletionTime(handlingReport.getCompletionTime(), errors);
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
        logger.error("Parse error in handling report: {}", errors);
        return ResponseEntity.badRequest().body("Invalid request: " + String.join(",", errors));
      }
    }
    return ResponseEntity.status(201).build();
  }
}
