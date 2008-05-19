package se.citerus.dddsample.service.dto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO for registering and routing a cargo.
 *
 */
public class CargoRoutingDTO {
  String trackingId;
  String origin;
  String finalDestination;
  List<LegDTO> legs;

  public CargoRoutingDTO(String trackingId, String origin, String finalDestination) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.legs = new ArrayList<LegDTO>();
  }

  public String getTrackingId() {
    return trackingId;
  }

  public String getOrigin() {
    return origin;
  }

  public String getFinalDestination() {
    return finalDestination;
  }

  public void addLeg(String carrierMovementId, String from, String to) {
    legs.add(new LegDTO(carrierMovementId, from, to));
  }

  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public boolean isRouted() {
    return !legs.isEmpty();
  }

}
