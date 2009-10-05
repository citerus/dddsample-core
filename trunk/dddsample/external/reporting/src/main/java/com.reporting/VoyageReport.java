package com.reporting;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.ArrayList;

@XmlRootElement(name = "voyage")
public class VoyageReport {

  private String voyageNumber;
  private String nextStop;
  private String etaNextStop;
  private String currentStatus;
  private int delayedByMinutes;
  private String lastUpdatedOn;
  private List<Cargo> onboardCargos = new ArrayList<Cargo>();

  public String getVoyageNumber() {
    return voyageNumber;
  }

  public void setVoyageNumber(String voyageNumber) {
    this.voyageNumber = voyageNumber;
  }

  public String getNextStop() {
    return nextStop;
  }

  public void setNextStop(String nextStop) {
    this.nextStop = nextStop;
  }

  public String getEtaNextStop() {
    return etaNextStop;
  }

  public void setEtaNextStop(String etaNextStop) {
    this.etaNextStop = etaNextStop;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(String currentStatus) {
    this.currentStatus = currentStatus;
  }

  public int getDelayedByMinutes() {
    return delayedByMinutes;
  }

  public void setDelayedByMinutes(int delayedByMinutes) {
    this.delayedByMinutes = delayedByMinutes;
  }

  public String getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(String lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
  }

  public List<Cargo> getOnboardCargos() {
    return onboardCargos;
  }

  public void setOnboardCargos(List<Cargo> onboardCargos) {
    this.onboardCargos = onboardCargos;
  }

  public static class Cargo {
    String trackingId;
    String finalDestination;

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

}
