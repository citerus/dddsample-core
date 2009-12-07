package se.citerus.dddsample.tracking.booking.api;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for a leg in an itinerary.
 */
public final class LegDTO implements Serializable {

  private final String voyageNumber;
  private final String from;
  private final String to;
  private final Date loadTime;
  private final Date unloadTime;

  /**
   * Constructor.
   *
   * @param voyageNumber
   * @param from
   * @param to
   * @param loadTime
   * @param unloadTime
   */
  public LegDTO(final String voyageNumber, final String from, final String to, final Date loadTime, final Date unloadTime) {
    this.voyageNumber = voyageNumber;
    this.from = from;
    this.to = to;
    this.loadTime = new Date(loadTime.getTime());
    this.unloadTime = new Date(unloadTime.getTime());
  }

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

  public Date getLoadTime() {
    return new Date(loadTime.getTime());
  }

  public Date getUnloadTime() {
    return new Date(unloadTime.getTime());
  }

}
