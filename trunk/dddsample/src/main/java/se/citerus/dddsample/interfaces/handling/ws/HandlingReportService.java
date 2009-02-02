package se.citerus.dddsample.interfaces.handling.ws;

import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * Web service endpoint for handling report submissions.
 *
 */
@WebService
public interface HandlingReportService {

  /**
   * Submits a report of handled cargos.
   *
   * @param handlingReport handling report
   * @throws HandlingReportErrors if there are formatting errors in the handling report
   */
  void submitReport(@WebParam HandlingReport handlingReport) throws HandlingReportErrors;

}
