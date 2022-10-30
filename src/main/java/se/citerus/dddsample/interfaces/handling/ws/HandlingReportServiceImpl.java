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
import se.citerus.dddsample.interfaces.handling.HandlingReportParser;

import javax.validation.Valid;
import java.lang.invoke.MethodHandles;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static se.citerus.dddsample.interfaces.handling.HandlingReportParser.*;

/**
 * This web service endpoint implementation performs basic validation and parsing
 * of incoming data, and in case of a valid registration attempt, sends an asynchronous message
 * with the information to the handling event registration system for proper registration.
 */
@RestController
public class HandlingReportServiceImpl implements HandlingReportService {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final ApplicationEvents applicationEvents;

  public HandlingReportServiceImpl(ApplicationEvents applicationEvents) {
    this.applicationEvents = applicationEvents;
  }

  @PostMapping(value = "/handlingReport", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
  @Override
  public ResponseEntity<?> submitReport(@Valid @RequestBody HandlingReport handlingReport) {
    try {
      Date completionTime = parseCompletionTime(handlingReport.getCompletionTime());
      VoyageNumber voyageNumber = parseVoyageNumber(handlingReport.getVoyageNumber());
      HandlingEvent.Type type = parseEventType(handlingReport.getType());
      UnLocode unLocode = parseUnLocode(handlingReport.getUnLocode());
      List<TrackingId> trackingIds = handlingReport.trackingIds.stream()
              .map(HandlingReportParser::parseTrackingId)
              .collect(Collectors.toList());

      for (TrackingId trackingId : trackingIds) {
        HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
                new Date(), completionTime, trackingId, voyageNumber, type, unLocode
        );

        applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
      }
    } catch (Exception e) {
      logger.error("Unexpected error in submitReport", e);
      return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
    }
    return ResponseEntity.status(201).build();
  }
}
