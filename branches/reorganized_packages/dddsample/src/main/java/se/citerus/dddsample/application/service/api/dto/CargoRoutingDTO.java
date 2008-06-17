package se.citerus.dddsample.application.service.api.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO for registering and routing a cargo.
 */
public final class CargoRoutingDTO implements Serializable {

  private final String trackingId;
  private final String origin;
  private final String finalDestination;
  private final List<LegDTO> legs;

  /**
   * Constructor.
   *
   * @param trackingId
   * @param origin
   * @param finalDestination
   */
  public CargoRoutingDTO(final String trackingId, final String origin, final String finalDestination) {
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

  public void addLeg(final String carrierMovementId, final String from, final String to) {
    legs.add(new LegDTO(carrierMovementId, from, to));
  }

  /**
   * @return An unmodifiable list DTOs.
   */
  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public boolean isRouted() {
    return !legs.isEmpty();
  }

}
