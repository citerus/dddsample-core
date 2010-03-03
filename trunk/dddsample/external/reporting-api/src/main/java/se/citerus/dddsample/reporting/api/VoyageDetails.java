package se.citerus.dddsample.reporting.api;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

import static se.citerus.dddsample.reporting.api.DateFormats.US_FORMAT;

@SuppressWarnings("UnusedDeclaration")
@XmlRootElement
public class VoyageDetails {

  private String voyageNumber;
  private String nextStop;
  private Date etaNextStop;
  private String currentStatus;
  private int delayedByMinutes;
  private Date lastUpdatedOn;

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

  @XmlElement(name = "etaNextStop")
  public String getEtaNextStopAsString() {
    return US_FORMAT.format(getEtaNextStop());
  }

  @XmlTransient
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

  @XmlElement(name = "lastUpdatedOn")
  public String getLastUpdatedOnAsString() {
    return US_FORMAT.format(getLastUpdatedOn());
  }

  @XmlTransient
  public Date getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(Date lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
  }

}
