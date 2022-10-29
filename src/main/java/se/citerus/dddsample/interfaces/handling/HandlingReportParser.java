package se.citerus.dddsample.interfaces.handling;

import org.apache.commons.lang.StringUtils;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;

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
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to parse UNLO code: " + unlocode, e);
    }
  }

  public static TrackingId parseTrackingId(final String trackingId) {
    try {
      return new TrackingId(trackingId);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Failed to parse trackingId: " + trackingId, e);
    }
  }

  public static VoyageNumber parseVoyageNumber(final String voyageNumber) {
    if (StringUtils.isNotEmpty(voyageNumber)) {
      try {
        return new VoyageNumber(voyageNumber);
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Failed to parse voyage number: " + voyageNumber, e);
      }
    } else {
      return null;
    }
  }

  public static Date parseDate(final String completionTime) {
    try {
      return SIMPLE_DATE_FORMAT.parse(completionTime);
    } catch (ParseException | NullPointerException e) {
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

  public static Date parseCompletionTime(LocalDateTime completionTime) {
    if (completionTime == null) {
      throw new IllegalArgumentException("Completion time is required");
    }

    return new Date(completionTime.toEpochSecond(ZoneOffset.UTC) * 1000);
  }
}
