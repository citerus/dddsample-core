package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;

/**
 * DTO for a leg in an itinerary.
 */
public final class LegDTO implements Serializable {

  private final String voyageNumber;
  private final String from;
  private final String to;

  /**
   * Constructor.
   *
   * @param voyageNumber
   * @param from
   * @param to
   */
  public LegDTO(final String voyageNumber, final String from, final String to) {
    this.voyageNumber = voyageNumber;
    this.from = from;
    this.to = to;
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

}
