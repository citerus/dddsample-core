package se.citerus.dddsample.service.dto;

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
  String currentLocation;
  List<HandlingEventDTO> events;

  public CargoWithHistoryDTO(String trackingId, String origin, String finalDestination, String currentLocation) {
    this.trackingId = trackingId;
    this.origin = origin;
    this.finalDestination = finalDestination;
    this.currentLocation = currentLocation;

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

  public String getCurrentLocation() {
    return currentLocation;
  }
}
