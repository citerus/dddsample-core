package se.citerus.dddsample.interfaces.handling;

import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
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

  public static UnLocode parseUnLocode(final String unlocode, final List<String> errors) {
    try {
      return new UnLocode(unlocode);
    } catch (IllegalArgumentException e) {
      errors.add(e.getMessage());
      return null;
    }
  }

  public static List<HandlingEventRegistrationAttempt> parse(final HandlingReport handlingReport,final List<String> errors){
    final Date completionTime = parseCompletionTime(handlingReport.getCompletionTime(), errors);
    final VoyageNumber voyageNumber = parseVoyageNumber(handlingReport.getVoyageNumber(), errors);
    final Type type = parseEventType(handlingReport.getType(), errors);
    final UnLocode unLocode = parseUnLocode(handlingReport.getUnLocode(), errors);
    final List<TrackingId> trackingIds = parseTrackingIds(handlingReport.getTrackingIds(),errors);
    if(errors.isEmpty()){
      return trackingIds.stream().map(trackingId->new HandlingEventRegistrationAttempt(
              new Date(), completionTime, trackingId, voyageNumber, type, unLocode
      )).collect(toList());
    }else {
      return emptyList();
    }
  }

  public static List<TrackingId> parseTrackingIds(final List<String> trackingIdStrs, final List<String> errors) {
      return Optional.ofNullable(trackingIdStrs)
              .orElse(emptyList())
              .stream()
              .map(trackingIdStr->parseTrackingId(trackingIdStr,errors))
              .filter(Objects::nonNull)
              .collect(toList());

  }

  public static TrackingId parseTrackingId(final String trackingId, final List<String> errors) {
    try {
      return new TrackingId(trackingId);
    } catch (IllegalArgumentException e) {
      errors.add(e.getMessage());
      return null;
    }
  }

  public static VoyageNumber parseVoyageNumber(final String voyageNumber, final List<String> errors) {
    if (voyageNumber != null && !voyageNumber.isBlank()) {
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

  public static Date parseDate(final String completionTime, final List<String> errors) {
    Date date;
    try {
      date = new SimpleDateFormat(ISO_8601_FORMAT).parse(completionTime);
    } catch (ParseException e) {
      errors.add("Invalid date format: " + completionTime + ", must be on ISO 8601 format: " + ISO_8601_FORMAT);
      date = null;
    }
    return date;
  }

  public static Type parseEventType(final String eventType, final List<String> errors) {
    try {
      return Type.valueOf(eventType);
    } catch (IllegalArgumentException e) {
      errors.add(eventType + " is not a valid handling event type. Valid types are: " + Arrays.toString(Type.values()));
      return null;
    }
  }

  public static Date parseCompletionTime(LocalDateTime completionTime, List<String> errors) {
    if (completionTime == null) {
      errors.add("Completion time is required");
      return null;
    }

    return new Date(completionTime.toEpochSecond(ZoneOffset.UTC) * 1000);
  }
}
