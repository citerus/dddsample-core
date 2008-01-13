package se.citerus.dddsample.service.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for a handling event.
 */
public class HandlingEventDTO implements Serializable {

  private final String type;
  private final String location;
  private final String carrier;
  private final Date time;

  public HandlingEventDTO(String location, String type, String carrier, Date time) {
    this.location = location;
    this.type = type;
    this.carrier = carrier;
    this.time = time;
  }

  public String getLocation() {
    return location;
  }

  public Date getTime() {
    return time;
  }

  public String getType() {
    return type;
  }

  public String getCarrier() {
    return carrier;
  }
}
