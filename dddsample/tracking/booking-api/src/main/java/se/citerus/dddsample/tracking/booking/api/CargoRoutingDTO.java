package se.citerus.dddsample.tracking.booking.api;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DTO for registering and routing a cargo.
 */
public final class CargoRoutingDTO implements Serializable {

  private final String trackingId;
  private final String origin;
  private final String finalDestination;
  private final Date arrivalDeadline;
  private final boolean misrouted;
  private final List<LegDTO> legs;

  /**
   * Constructor.
   *
   * @param trackingId
   * @param origin
   * @param finalDestination
   * @param arrivalDeadline
   * @param misrouted
   * @param legs
   */
  public CargoRoutingDTO(final String trackingId, final String origin, final String finalDestination,
                         final Date arrivalDeadline, final boolean misrouted, final List<LegDTO> legs) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.arrivalDeadline = arrivalDeadline;
    this.misrouted = misrouted;
    this.legs = legs;
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

  /**
   * @return An unmodifiable list DTOs.
   */
  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public boolean isMisrouted() {
    return misrouted;
  }

  public Date getArrivalDeadline() {
    return arrivalDeadline;
  }

}
