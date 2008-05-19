package se.citerus.dddsample.service.dto;

/**
 * DTO for a leg in an itinerary.
 *
 */
public class LegDTO {

  String carrierMovementId;
  String from;
  String to;

  public LegDTO(String carrierMovementId, String from, String to) {
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
