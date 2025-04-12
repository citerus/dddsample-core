package se.citerus.dddsample.interfaces.booking.facade.dto;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
  private final Instant arrivalDeadline;
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
   */
  public CargoRoutingDTO(String trackingId, String origin, String finalDestination, Instant arrivalDeadline, boolean misrouted) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.arrivalDeadline = arrivalDeadline;
    this.misrouted = misrouted;
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

  public void addLeg(String voyageNumber, String from, String to, Instant loadTime, Instant unloadTime) {
    legs.add(new LegDTO(voyageNumber, from, to, loadTime, unloadTime));
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

  public boolean isRouted() {
    return !legs.isEmpty();
  }

  public ZonedDateTime getArrivalDeadline() {
    return arrivalDeadline.atZone(ZoneOffset.UTC);
  }
}
