package se.citerus.dddsample.application.util;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * A few utils for working with Date in tests.
 *
 */
public final class DateUtils {

  /**
   * @param date date string as yyyy-MM-dd
   * @return Date representation
   */
  public static Instant toDate(final String date) {
    return toDate(date, "00:00");
  }

  /**
   * @param date date string as yyyy-MM-dd
   * @param time time string as HH:mm
   * @return Date representation
   */
  public static Instant toDate(final String date, final String time) {
    try {
      return Instant.parse(date + "T" + time + ":00Z");
    } catch (DateTimeParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Prevent instantiation.
   */
  private DateUtils() {
  }
}
