package com.partner.pathfinder.api;

import java.io.Serializable;

/**
 * DTO for a leg in an itinerary.
 */
public final class TransitEdge implements Serializable {

  private final String carrierMovementId;
  private final String fromUnLocode;
  private final String toUnLocode;

  /**
   * Constructor.
   *
   * @param carrierMovementId
   * @param fromUnLocode
   * @param toUnLocode
   */
  public TransitEdge(final String carrierMovementId, final String fromUnLocode, final String toUnLocode) {
    this.carrierMovementId = carrierMovementId;
    this.fromUnLocode = fromUnLocode;
    this.toUnLocode = toUnLocode;
  }

  public String getCarrierMovementId() {
    return carrierMovementId;
  }

  public String getFromUnLocode() {
    return fromUnLocode;
  }

  public String getToUnLocode() {
    return toUnLocode;
  }

}