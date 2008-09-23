package se.citerus.registerapp.service;

import se.citerus.dddsample.application.ws.HandlingEventServiceEndpoint;


public class HandlingEventServiceWs implements HandlingEventService {
  private HandlingEventServiceEndpoint endPoint;
  
  public void register(String completionTime, String trackingId, String carrierMovementId, String unlocode, String eventType){
    System.out.println(completionTime);
    endPoint.register(completionTime, trackingId, carrierMovementId, unlocode, eventType);
  }

  public void setEndPoint(HandlingEventServiceEndpoint endPoint) {
    this.endPoint = endPoint;
  }
}
