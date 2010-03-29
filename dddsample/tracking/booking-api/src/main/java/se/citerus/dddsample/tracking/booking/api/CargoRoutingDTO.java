package se.citerus.dddsample.tracking.booking.api;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DTO for registering and routing a cargo.
 */
public final class
  CargoRoutingDTO implements Serializable {

  private final String trackingId;
  private final String origin;
  private final String finalDestination;
  private final Date arrivalDeadline;
  private final boolean misrouted;
  private final List<LegDTO> legs;

  public CargoRoutingDTO(final String trackingId, final String origin, final String finalDestination,
                         final Date arrivalDeadline, final boolean misrouted, final List<LegDTO> legs) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.arrivalDeadline = new Date(arrivalDeadline.getTime());
    this.misrouted = misrouted;
    this.legs = new ArrayList<LegDTO>(legs);
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

  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }

  public boolean isMisrouted() {
    return misrouted;
  }

  public boolean isRouted() {
    return !legs.isEmpty();
  }

  public Date getArrivalDeadline() {
    return new Date(arrivalDeadline.getTime());
  }

}
