package se.citerus.dddsample.service.dto;

import se.citerus.dddsample.domain.StatusCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * DTO for a cargo and its delivery history.
 */
public final class CargoTrackingDTO implements Serializable {

  private final String trackingId;
  private final String origin;
  private final String finalDestination;
  private final String currentLocationId;
  private final List<HandlingEventDTO> events;
  private final String carrierMovementId;
  private final StatusCode statusCode;
  private final boolean misdirected;

  /**
   * Constructor.
   *
   * @param trackingId
   * @param origin
   * @param finalDestination
   * @param statusCode
   * @param currentLocationId
   * @param carrierMovementId
   * @param isMisdirected
   */
  public CargoTrackingDTO(final String trackingId, final String origin, final String finalDestination,
                          final StatusCode statusCode, final String currentLocationId, final String carrierMovementId,
                          final boolean isMisdirected) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.statusCode = statusCode;
    this.currentLocationId = currentLocationId;
    this.carrierMovementId = carrierMovementId;
    this.misdirected = isMisdirected;

    this.events = new ArrayList<HandlingEventDTO>();
  }

  public void addEvent(final String location, final String type, final String carrier, final Date time,
                       final boolean expected) {
    events.add(new HandlingEventDTO(location, type, carrier, time, expected));
  }

  /**
   * @return An unmodifiable list DTOs.
   */
  public List<HandlingEventDTO> getEvents() {
    return Collections.unmodifiableList(events);
  }

  public String getFinalDestination() {
    return finalDestination;
  }

  public String getOrigin() {
    return origin;
  }

  public String getTrackingId() {
    return trackingId;
  }

  public String getCurrentLocationId() {
    return currentLocationId;
  }

  public StatusCode getStatusCode() {
    return statusCode;
  }

  public String getCarrierMovementId() {
    return carrierMovementId;
  }

  public boolean isMisdirected() {
    return misdirected;
  }

}
