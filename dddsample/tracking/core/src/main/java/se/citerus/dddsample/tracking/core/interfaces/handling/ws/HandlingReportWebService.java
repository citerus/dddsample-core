package se.citerus.dddsample.tracking.core.interfaces.handling.ws;

import com.aggregator.HandlingReport;
import com.aggregator.HandlingReportErrors;
import com.aggregator.HandlingReportErrors_Exception;
import com.aggregator.HandlingReportService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import se.citerus.dddsample.tracking.core.application.handling.HandlingEventService;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.OperatorCode;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import static se.citerus.dddsample.tracking.core.interfaces.handling.HandlingReportParser.*;

import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.*;

/**
 * This web service endpoint implementation performs basic validation and parsing
 * of an incoming handling report, and attempts to register proper handling events
 * by calling the application layer.
 */
@WebService(endpointInterface = "com.aggregator.HandlingReportService")
@Service
public class HandlingReportWebService implements HandlingReportService {

  private HandlingEventService handlingEventService;
  private final static Log logger = LogFactory.getLog(HandlingReportWebService.class);

  @Override
  public void submitReport(@WebParam(name = "arg0", targetNamespace = "") HandlingReport handlingReport) throws HandlingReportErrors_Exception {
    final List<String> validationErrors = new ArrayList<String>();

    final Date completionTime = parseCompletionTime(handlingReport, validationErrors);
    final VoyageNumber voyageNumber = parseVoyageNumber(handlingReport.getVoyageNumber(), validationErrors);
    final HandlingEvent.Type type = parseEventType(handlingReport.getType(), validationErrors);
    final UnLocode unLocode = parseUnLocode(handlingReport.getUnLocode(), validationErrors);
    final OperatorCode operatorCode = parseOperatorCode();

    final Map<String, String> allErrors = new HashMap<String, String>();
    for (String trackingIdStr : handlingReport.getTrackingIds()) {
      final TrackingId trackingId = parseTrackingId(trackingIdStr, validationErrors);

      if (validationErrors.isEmpty()) {
        try {
          handlingEventService.registerHandlingEvent(completionTime, trackingId, voyageNumber, unLocode, type, operatorCode);
        } catch (CannotCreateHandlingEventException e) {
          logger.error(e, e);
          allErrors.put(trackingIdStr, e.getMessage());
        }
      } else {
        logger.error("Parse error in handling report: " + validationErrors);
        allErrors.put(trackingIdStr, validationErrors.toString());
      }
    }

    if (!allErrors.isEmpty()) {
      final HandlingReportErrors faultInfo = new HandlingReportErrors();
      throw new HandlingReportErrors_Exception(createErrorMessage(allErrors), faultInfo);
    }
  }

  private String createErrorMessage(final Map<String, String> allErrors) {
    final StringBuilder sb = new StringBuilder("--- BEGIN HANDLING REPORT ERRORS ---\n");
    for (Map.Entry<String, String> e : allErrors.entrySet()) {
      sb.append(e.getKey()).append(" : ").append(e.getValue()).append("\n");
    }
    sb.append("--- END HANDLING REPORT ERRORS ---");
    return sb.toString();
  }

  public void setHandlingEventService(final HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }

}
