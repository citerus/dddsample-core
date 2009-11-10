package se.citerus.dddsample.reporting.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OnboardCargo {
  
  private String trackingId;
  private String finalDestination;

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(String trackingId) {
    this.trackingId = trackingId;
  }

  public String getFinalDestination() {
    return finalDestination;
  }

  public void setFinalDestination(String finalDestination) {
    this.finalDestination = finalDestination;
  }

}
