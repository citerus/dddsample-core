package se.citerus.dddsample.service.dto;

/**
 * DTO for a leg in an itinerary.
 */
public final class LegDTO {

  private final String carrierMovementId;
  private final String from;
  private final String to;

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
