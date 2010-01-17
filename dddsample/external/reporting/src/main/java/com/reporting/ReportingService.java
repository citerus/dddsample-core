package com.reporting;

import com.reporting.db.ReportDAO;
import com.reporting.reports.CargoReport;
import com.reporting.reports.VoyageReport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;
import se.citerus.dddsample.reporting.api.ReportSubmission;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.text.ParseException;

import static com.reporting.Constants.US_DATETIME;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

@Produces({"application/json", "application/pdf"})
@Consumes({"application/json", "application/xml"})
@Path("/")
public class ReportingService implements ReportSubmission {

  private ReportDAO reportDAO;
  private static final Log LOG = LogFactory.getLog(ReportingService.class);

  public ReportingService(ReportDAO reportDAO) {
    this.reportDAO = reportDAO;
  }

  @GET
  @Path("/cargo/{trackingId}.{format}")
  public Response getCargoReport(@PathParam("trackingId") String trackingId, @PathParam("format") String format) throws ParseException {
    CargoReport cargoReport = reportDAO.loadCargoReport(trackingId);
    if (cargoReport == null) return status(404).build();

    return ok(cargoReport).
      type("application/" + format).
      lastModified(US_DATETIME.parse(cargoReport.getCargo().getLastUpdatedOn())).
      build();
  }

  @GET
  @Path("/voyage/{voyageNumber}.{format}")
  public Response getVoyageReport(@PathParam("voyageNumber") String voyageNumber, @PathParam("format") String format) throws ParseException {
    VoyageReport voyageReport = reportDAO.loadVoyageReport(voyageNumber);
    if (voyageReport == null) return status(404).build();

    return ok(voyageReport).
      type("application/" + format).
      lastModified(US_DATETIME.parse(voyageReport.getVoyage().getLastUpdatedOn())).
      build();
  }

  @Override
  @PUT
  @Path("/cargo")
  @Transactional
  public void reportCargo(CargoDetails cargoDetails) {
    reportDAO.storeCargoDetals(cargoDetails);
    LOG.info("Stored cargo: " + cargoDetails);
  }

  @Override
  @POST
  @Path("/cargo/{trackingId}/handled")
  @Transactional
  public void reportHandling(@PathParam("trackingId") String trackingId, Handling handling) {
    reportDAO.storeHandling(trackingId, handling);
    LOG.info("Stored handling of cargo " + trackingId + ": " + handling);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  ReportingService() {
    // Needed by CGLIB
  }

}
