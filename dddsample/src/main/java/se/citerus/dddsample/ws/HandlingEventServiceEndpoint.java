package se.citerus.dddsample.ws;

import javax.jws.WebService;

/**
 * Web service endpoint for handling event registration.
 */
@WebService
public interface HandlingEventServiceEndpoint {

  /**
   * Register an cargo handling event.
   *
   * @param completionTime    time when event occured, for example a the loading of cargo was completed
   * @param trackingId        tracking id of the cargo
   * @param carrierMovementId carrier movement id, if applicable
   * @param unlocode          United Nations Location Code for the location where the event occured
   * @param eventType         type of event
   */
  void register(String completionTime, String trackingId, String carrierMovementId, String unlocode, String eventType);

}
