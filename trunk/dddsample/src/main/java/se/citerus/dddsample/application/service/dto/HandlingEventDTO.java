package se.citerus.dddsample.application.service.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for a handling event.
 */
public final class HandlingEventDTO implements Serializable {

  private final String type;
  private final String location;
  private final String carrier;
  private final Date time;
  private boolean expected;

  /**
   * Constructor.
   *
   * @param location
   * @param type
   * @param carrier
   * @param time
   * @param expected
   */
  public HandlingEventDTO(final String location, final String type, final String carrier, final Date time,
                          final boolean expected) {
    this.location = location;
    this.type = type;
    this.carrier = carrier;
    this.time = time;
    this.expected = expected;
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

  public boolean isExpected() {
    return expected;
  }
}
