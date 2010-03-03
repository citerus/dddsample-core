package se.citerus.dddsample.reporting.api;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

import static se.citerus.dddsample.reporting.api.DateFormats.US_FORMAT;

@XmlRootElement
public class CargoDetails {

  private String trackingId;
  private String receivedIn;
  private String finalDestination;
  private Date arrivalDeadline;
  private Date eta;
  private String currentStatus;
  private String currentVoyage;
  private String currentLocation;
  private Date lastUpdatedOn;

  public String getTrackingId() {
    return trackingId;
  }

  public void setTrackingId(String trackingId) {
    this.trackingId = trackingId;
  }

  public String getReceivedIn() {
    return receivedIn;
  }

  public void setReceivedIn(String receivedIn) {
    this.receivedIn = receivedIn;
  }

  public String getFinalDestination() {
    return finalDestination;
  }

  public void setFinalDestination(String finalDestination) {
    this.finalDestination = finalDestination;
  }

  @XmlElement(name = "arrivalDeadline")
  @SuppressWarnings("UnusedDeclaration")
  public String getArrivalDeadlineAsString() {
    return US_FORMAT.format(getArrivalDeadline());
  }

  @XmlTransient
  public Date getArrivalDeadline() {
    return arrivalDeadline;
  }

  public void setArrivalDeadline(Date arrivalDeadline) {
    this.arrivalDeadline = arrivalDeadline;
  }

  @XmlElement(name = "eta")
  @SuppressWarnings("UnusedDeclaration")
  public String getEtaAsString() {
    return US_FORMAT.format(getEta());
  }

  @XmlTransient
  public Date getEta() {
    return eta;
  }

  public void setEta(Date eta) {
    this.eta = eta;
  }

  public String getCurrentStatus() {
    return currentStatus;
  }

  public void setCurrentStatus(String currentStatus) {
    this.currentStatus = currentStatus;
  }

  public String getCurrentVoyage() {
    return currentVoyage;
  }

  public void setCurrentVoyage(String currentVoyage) {
    this.currentVoyage = currentVoyage;
  }

  public String getCurrentLocation() {
    return currentLocation;
  }

  public void setCurrentLocation(String currentLocation) {
    this.currentLocation = currentLocation;
  }

  @XmlElement(name = "lastUpdatedOn")
  @SuppressWarnings("UnusedDeclaration")
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

  @Override
  public boolean equals(Object that) {
    return EqualsBuilder.reflectionEquals(this, that);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
