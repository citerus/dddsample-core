package se.citerus.dddsample.tracking.booking.api;

import java.io.Serializable;

public class CarrierMovementDTO implements Serializable {

  private final LocationDTO departureLocation;
  private final LocationDTO arrivalLocation;

  public CarrierMovementDTO(LocationDTO departureLocation, LocationDTO arrivalLocation) {
    this.departureLocation = departureLocation;
    this.arrivalLocation = arrivalLocation;
  }

  public LocationDTO getDepartureLocation() {
    return departureLocation;
  }

  public LocationDTO getArrivalLocation() {
    return arrivalLocation;
  }
}
