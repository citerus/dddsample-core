package se.citerus.dddsample.reporting.api;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class VoyageDetails {

  private String voyageNumber;
  private String nextStop;
  private String etaNextStop;
  private String currentStatus;
  private int delayedByMinutes;
  private String lastUpdatedOn;

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

}
