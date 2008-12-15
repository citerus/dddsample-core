package se.citerus.dddsample.interfaces.handling.ws.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.interfaces.handling.RegistrationParser;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReport;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportErrors;
import se.citerus.dddsample.interfaces.handling.ws.HandlingReportService;

import javax.jws.WebService;
import java.util.Date;

/**
 * This web service endpoint implementation performs basic validation and parsing
 * of incoming data, and in case of a valid registration attempt, sends an asynchronous message
 * with the informtion to the handling event registration system for proper registration.
 *  
 */
@WebService(endpointInterface = "se.citerus.dddsample.interfaces.handling.ws.HandlingReportService")
public class HandlingReportServiceImpl implements HandlingReportService {

  private RegistrationParser registrationParser;
  private static final Log logger = LogFactory.getLog(HandlingReportServiceImpl.class);
  
  public static final String ISO_8601_FORMAT = "yyyy-mm-dd HH:MM:SS.SSS";

  @Override
  public void submitReport(HandlingReport handlingReport) throws HandlingReportErrors {
    Date date = handlingReport.getCompletionTime().toGregorianCalendar().getTime();
    for (String trackingId : handlingReport.getTrackingIds()) {
      registrationParser.convertAndSend(
        "", trackingId, handlingReport.getVoyageNumber(), handlingReport.getUnLocode(), handlingReport.getType()
      );
    }
  }

  public void setRegistrationParser(RegistrationParser registrationParser) {
    this.registrationParser = registrationParser;
  }

}
