package se.citerus.dddsample.interfaces.handling.ws;

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
   * @throws HandlingReportErrors
   */
  void submitReport(HandlingReport handlingReport) throws HandlingReportErrors;

}
