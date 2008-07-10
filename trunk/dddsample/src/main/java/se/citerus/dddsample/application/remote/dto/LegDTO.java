package se.citerus.dddsample.application.remote.dto;

import java.io.Serializable;

/**
 * DTO for a leg in an itinerary.
 */
public final class LegDTO implements Serializable {

  private final String carrierMovementId;
  private final String from;
  private final String to;

  /**
   * Constructor.
   *
   * @param carrierMovementId
   * @param from
   * @param to
   */
  public LegDTO(final String carrierMovementId, final String from, final String to) {
    this.carrierMovementId = carrierMovementId;
    this.from = from;
    this.to = to;
  }

  public String getCarrierMovementId() {
    return carrierMovementId;
  }

  public String getFrom() {
    return from;
  }

  public String getTo() {
    return to;
  }

}
