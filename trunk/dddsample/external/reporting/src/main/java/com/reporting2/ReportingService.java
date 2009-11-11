package com.reporting2;

import static com.reporting2.Constants.US_DATETIME;
import com.reporting2.db.ReportDAO;
import com.reporting2.reports.CargoReport;
import com.reporting2.reports.VoyageReport;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;
import java.text.ParseException;

@Produces({"application/json", "application/pdf"})
@Consumes("application/json")
@Path("/")
public class ReportingService {

  private ReportDAO reportDAO;

  public ReportingService(ReportDAO reportDAO) {
    this.reportDAO = reportDAO;
  }

  @GET
  @Path("/cargo/{trackingId}")
  public Response getCargoReport(@PathParam("trackingId") String trackingId) throws ParseException {
    CargoReport cargoReport = reportDAO.loadCargoReport(trackingId);
    if (cargoReport == null) return status(404).build();

    return ok(cargoReport).
      lastModified(US_DATETIME.parse(cargoReport.getCargo().getLastUpdatedOn())).
      build();
  }

  @GET
  @Path("/voyage/{voyageNumber}")
  public Response getVoyageReport(@PathParam("voyageNumber") String voyageNumber) throws ParseException {
    VoyageReport voyageReport = reportDAO.loadVoyageReport(voyageNumber);
    if (voyageReport == null) return status(404).build();

    return ok(voyageReport).
      lastModified(US_DATETIME.parse(voyageReport.getVoyage().getLastUpdatedOn())).
      build();
  }

  @PUT
  @Path("/cargo")
  public void reportCargoDetails(CargoDetails cargoDetails) {
    // TODO
  }

  @PUT
  @Path("/cargo/{trackingId}/handled")
  public void reportHandling(@PathParam("trackingId") String trackingId, Handling handling) {
    // TODO
  }

  ReportingService() {
    // Needed by CGLIB
  }

}
