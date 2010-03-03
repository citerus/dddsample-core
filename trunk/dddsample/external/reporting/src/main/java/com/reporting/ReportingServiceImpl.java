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

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.ok;
import static javax.ws.rs.core.Response.status;

public class ReportingServiceImpl implements ReportSubmission, ReportingService {

  private ReportDAO reportDAO;
  private static final Log LOG = LogFactory.getLog(ReportingServiceImpl.class);

  public ReportingServiceImpl(ReportDAO reportDAO) {
    this.reportDAO = reportDAO;
  }

  @Override
  public Response getCargoReport(String trackingId, String format) {
    CargoReport cargoReport = reportDAO.loadCargoReport(trackingId);
    if (cargoReport == null) return status(404).build();

    return ok(cargoReport).
      type("application/" + format).
      lastModified(cargoReport.getCargo().getLastUpdatedOn()).
      build();
  }

  @Override
  public Response getVoyageReport(String voyageNumber, String format) {
    VoyageReport voyageReport = reportDAO.loadVoyageReport(voyageNumber);
    if (voyageReport == null) return status(404).build();

    return ok(voyageReport).
      type("application/" + format).
      lastModified(voyageReport.getVoyage().getLastUpdatedOn()).
      build();
  }

  @Override
  @Transactional
  public void submitCargoDetails(CargoDetails cargoDetails) {
    reportDAO.storeCargoDetals(cargoDetails);
    LOG.info("Stored cargo: " + cargoDetails);
  }

  @Override
  @Transactional
  public void submitHandling(String trackingId, Handling handling) {
    reportDAO.storeHandling(trackingId, handling);
    LOG.info("Stored handling of cargo " + trackingId + ": " + handling);
  }

  @SuppressWarnings({"UnusedDeclaration"})
  ReportingServiceImpl() {
    // Needed by CGLIB
  }

}
