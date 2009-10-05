package com.reporting;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "cargo")
public class CargoReport {

  String trackingId;
  String receivedIn;
  String finalDestination;
  String arrivalDeadline;
  String eta;
  String currentStatus;
  String currentVoyage;
  String currentLocation;
  String lastUpdatedOn;
  List<Handling> handlings;

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

  public List<Handling> getHandlings() {
    return handlings;
  }

  public void setHandlings(List<Handling> handlings) {
    this.handlings = handlings;
  }

  public String getLastUpdatedOn() {
    return lastUpdatedOn;
  }

  public void setLastUpdatedOn(String lastUpdatedOn) {
    this.lastUpdatedOn = lastUpdatedOn;
  }

  public static class Handling {
    String type;
    String location;
    String voyage;
    String completedOn;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getLocation() {
      return location;
    }

    public void setLocation(String location) {
      this.location = location;
    }

    public String getVoyage() {
      return voyage;
    }

    public void setVoyage(String voyage) {
      this.voyage = voyage;
    }

    public String getCompletedOn() {
      return completedOn;
    }

    public void setCompletedOn(String completedOn) {
      this.completedOn = completedOn;
    }
  }

}
