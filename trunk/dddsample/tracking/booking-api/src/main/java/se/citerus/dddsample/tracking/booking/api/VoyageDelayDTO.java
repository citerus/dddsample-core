package se.citerus.dddsample.tracking.booking.api;

import java.io.Serializable;

public class VoyageDelayDTO implements Serializable {

  private final String voyageNumber;
  private final int minutesOfDelay;

  public VoyageDelayDTO(String voyageNumber, int minutesOfDelay) {
    this.voyageNumber = voyageNumber;
    this.minutesOfDelay = minutesOfDelay;
  }

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public int getMinutesOfDelay() {
    return minutesOfDelay;
  }
}
