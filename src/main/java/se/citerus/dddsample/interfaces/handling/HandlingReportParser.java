package se.citerus.dddsample.interfaces.handling;

import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReport;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

/**
 * Utility methods for parsing various forms of handling report formats.
 * Supports the notification pattern for incremental error reporting.
 */
public class HandlingReportParser {

  public static final String ISO_8601_FORMAT = "yyyy-MM-dd HH:mm";
  public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(ISO_8601_FORMAT);

  public static UnLocode parseUnLocode(final String unlocode) {
    try {
      return new UnLocode(unlocode);
    } catch (IllegalArgumentException|NullPointerException e) {
      throw new IllegalArgumentException("Failed to parse UNLO code: " + unlocode, e);
    }
  }

  public static TrackingId parseTrackingId(final String trackingId) {
    try {
      return new TrackingId(trackingId);
    } catch (IllegalArgumentException|NullPointerException e) {
      throw new IllegalArgumentException("Failed to parse trackingId: " + trackingId, e);
    }
  }

  public static VoyageNumber parseVoyageNumber(final String voyageNumber) {
    if (voyageNumber != null && !voyageNumber.isBlank()) {
      try {
        return new VoyageNumber(voyageNumber);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Failed to parse voyage number: " + voyageNumber, e);
      }
    } else {
      return null;
    }
  }

  public static Instant parseDate(final String completionTime) {
    try {
      String[] parts = completionTime.split(" ");
      return LocalDate.parse(parts[0]).atTime(LocalTime.parse(parts[1])).toInstant(ZoneOffset.UTC);
    } catch (DateTimeParseException | NullPointerException e) {
      throw new IllegalArgumentException("Invalid date format: " + completionTime + ", must be on ISO 8601 format: " + ISO_8601_FORMAT);
    }
  }

  public static HandlingEvent.Type parseEventType(final String eventType) {
    try {
      return HandlingEvent.Type.valueOf(eventType);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(eventType + " is not a valid handling event type. Valid types are: " + Arrays.toString(HandlingEvent.Type.values()));
    }
  }

  public static Instant parseCompletionTime(LocalDateTime completionTime) {
    if (completionTime == null) {
      throw new IllegalArgumentException("Completion time is required");
    }

    return Instant.ofEpochSecond(completionTime.toEpochSecond(ZoneOffset.UTC));
  }

  public static List<HandlingEventRegistrationAttempt> parse(final HandlingReport handlingReport){
    final Instant completionTime = parseCompletionTime(handlingReport.getCompletionTime());
    final VoyageNumber voyageNumber = parseVoyageNumber(handlingReport.getVoyageNumber());
    final Type type = parseEventType(handlingReport.getType());
    final UnLocode unLocode = parseUnLocode(handlingReport.getUnLocode());
    final List<TrackingId> trackingIds = parseTrackingIds(handlingReport.getTrackingIds());
    return trackingIds.stream().map(trackingId -> new HandlingEventRegistrationAttempt(
            Instant.now(), completionTime, trackingId, voyageNumber, type, unLocode
    )).collect(toList());
  }

  public static List<TrackingId> parseTrackingIds(final List<String> trackingIdStrs) {
    return Optional.ofNullable(trackingIdStrs)
            .orElse(emptyList())
            .stream()
            .map(HandlingReportParser::parseTrackingId)
            .filter(Objects::nonNull)
            .collect(toList());
  }
}
