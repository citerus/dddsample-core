package se.citerus.dddsample.service;

import java.util.Date;

import se.citerus.dddsample.domain.Location;


public interface HandlingEventService {

  /**
   * Regster an event with the EventHandling service
   * 
   * TODO: Fix javadoc when we know more about the relation between Cargo and HandlingEvent...
   * 
   * @param date
   * @param type
   * @param location TODO
   * @param carrierId
   * @param trackId
   */
  public abstract void register(Date date, String type, Location location, String carrierId, String trackId);

}