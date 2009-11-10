package se.citerus.dddsample.reporting.api;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CargoDetails {

  private String trackingId;
  private String receivedIn;
  private String finalDestination;
  private String arrivalDeadline;
  private String eta;
  private String currentStatus;
  private String currentVoyage;
  private String currentLocation;
  private String lastUpdatedOn;

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

  public String getArrivalDeadline() {
    return arrivalDeadline;
  }

  public void setArrivalDeadline(String arrivalDeadline) {
    this.arrivalDeadline = arrivalDeadline;
  }

  public String getEta() {
    return eta;
  }

  public void setEta(String eta) {
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

  public String getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(String lastUpdatedOn) {
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
