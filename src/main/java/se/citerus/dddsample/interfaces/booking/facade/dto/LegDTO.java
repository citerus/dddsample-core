package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for a leg in an itinerary.
 */
public final class LegDTO implements Serializable {

  private final String voyageNumber;
  private final String from;
  private final String to;
  private final String loadTime;
  private final String unloadTime;

  /**
   * Constructor.
   *
   * @param voyageNumber
   * @param from
   * @param to
   * @param loadTime
   * @param unloadTime
   */
  public LegDTO(final String voyageNumber, final String from, final String to, String loadTime, String unloadTime) {
    this.voyageNumber = voyageNumber;
    this.from = from;
    this.to = to;
    this.loadTime = loadTime;
    this.unloadTime = unloadTime;
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

  public String getLoadTime() {
    return loadTime;
  }

  public String getUnloadTime() {
    return unloadTime;
  }
  
}
