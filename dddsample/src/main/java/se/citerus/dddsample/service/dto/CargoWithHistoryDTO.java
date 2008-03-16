package se.citerus.dddsample.service.dto;

import se.citerus.dddsample.domain.StatusCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO for a cargo and its delivery history.
 *
 */
public class CargoWithHistoryDTO implements Serializable {

  String trackingId;
  String origin;
  String finalDestination;
  String currentLocationId;
  List<HandlingEventDTO> events;
  String carrierMovementId;
  StatusCode statusCode;

  public CargoWithHistoryDTO(String trackingId, String origin, String finalDestination,
                             StatusCode statusCode, String currentLocationId, String carrierMovementId) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.statusCode = statusCode;
    this.currentLocationId = currentLocationId;
    this.carrierMovementId = carrierMovementId;

    this.events = new ArrayList<HandlingEventDTO>();
  }

  public void addEvent(HandlingEventDTO handlingEvent) {
    events.add(handlingEvent);
  }

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

}
