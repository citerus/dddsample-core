package se.citerus.dddsample.ws;

import javax.jws.WebService;

/**
 * Web service endpoint for handling event registration.
 *
 */
@WebService
public interface HandlingEventServiceEndpoint {

  void register(String completionTime, String trackingId, String carrierMovementId, String unlocode, String eventType);

}
