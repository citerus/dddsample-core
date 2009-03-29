package se.citerus.dddsample.application.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A few utils for working with Date in tests.
 *
 */
public final class DateTestUtil {

  /**
   * @param date date string as yyyy-MM-dd
   * @return Date representation
   */
  public static Date toDate(final String date) {
    return toDate(date, "00:00.00.000");
  }

  /**
   * @param date date string as yyyy-MM-dd
   * @param time time string as HH:mm
   * @return Date representation
   */
  public static Date toDate(final String date, final String time) {
    try {
      return new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(date + " " + time);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Prevent instantiation.
   */
  private DateTestUtil() {
  }
}
