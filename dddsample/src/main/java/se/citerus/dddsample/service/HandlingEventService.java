package se.citerus.dddsample.service;

import java.util.Date;


public interface HandlingEventService {

  /**
   * Regster an event with the EventHandling service
   * 
   * TODO: Fix javadoc when we know more about the relation between Cargo and HandlingEvent...
   * 
   * @param date
   * @param type
   * @param carrierId
   * @param trackIds
   */
  public abstract void register(Date date, String type, String carrierId, String[] trackIds);

}