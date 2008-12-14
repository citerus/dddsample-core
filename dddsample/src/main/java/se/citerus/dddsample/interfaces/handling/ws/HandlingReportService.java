package se.citerus.dddsample.interfaces.handling.ws;

import se.citerus.dddsample.interfaces.handling.RegistrationFailure;

import javax.jws.WebService;

/**
 * Web service endpoint for handling event registration.
 */
@WebService
public interface HandlingReportService {

  /**
   * Submits a report of handled cargos.
   *
   * @param handlingReport
   * @throws se.citerus.dddsample.interfaces.handling.RegistrationFailure
   */
  void submitReport(HandlingReport handlingReport) throws RegistrationFailure;

}
