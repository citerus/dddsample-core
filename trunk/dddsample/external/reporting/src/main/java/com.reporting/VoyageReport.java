package com.reporting;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "voyage")
public class VoyageReport {

  private Date lastUpdatedOn;
  private String voyageNumber;
  private String nextStop;
  private Date etaNextStop;
  private String currentStatus;
  private int delayedByMinutes;

  public Date getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(Date lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
  }

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

  public Date getEtaNextStop() {
    return etaNextStop;
  }

  public void setEtaNextStop(Date etaNextStop) {
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
}
