package com.reporting;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.DataAccessException;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Context;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;

@Produces("application/json")
@Path("/report")
public class ReportingService {

  private JdbcTemplate jdbc;
  private HandlingRowMapper handlingRowMapper;
  private CargoReportRowMapper cargoReportRowMapper;
  private VoyageReportRowMapper rowMapper;

  public ReportingService(DataSource dataSource) {
    this.jdbc = new JdbcTemplate(dataSource);
    this.handlingRowMapper = new HandlingRowMapper();
    this.cargoReportRowMapper = new CargoReportRowMapper();
    this.rowMapper = new VoyageReportRowMapper();
  }

  @GET
  @Path("/cargo/{trackingId}")
  @Transactional
  public CargoReport getCargoReport(@PathParam("trackingId") String trackingId, @Context Response.ResponseBuilder response) {
    CargoReport cargoReport = loadCargoReport(trackingId);
    if (cargoReport == null) return null;

    cargoReport.setHandlings(loadHandlings(trackingId));
    //response.lastModified(cargoReport.getLastUpdatedOn());
    return cargoReport;
  }

  @GET
  @Path("/voyage/{voyageNumber}")
  @Transactional
  public VoyageReport getVoyageReport(@PathParam("voyageNumber") String voyageNumber, @Context Response.ResponseBuilder response) {
    VoyageReport voyageReport = loadVoyageReport(voyageNumber);
    if (voyageReport == null) return null;

    //response.lastModified(voyageReport.getLastUpdatedOn());
    return voyageReport;
  }

  // --- Private methods ---

  private CargoReport loadCargoReport(String trackingId) {
    try {
      return (CargoReport) jdbc.queryForObject("select * from cargo where cargo_tracking_id = ?", new String[] {trackingId}, cargoReportRowMapper);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  private List<CargoReport.Handling> loadHandlings(String trackingId) {
    final List<CargoReport.Handling> handlings = new ArrayList<CargoReport.Handling>();
    RowCallbackHandler handler = new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        handlings.add(handlingRowMapper.mapRow(rs, rs.getRow()));
      }
    };
    String[] args = {trackingId};
    String sql = "select * from handling where cargo_tracking_id = ?";
    jdbc.query(sql, args, handler);
    return handlings;
  }

  private VoyageReport loadVoyageReport(String voyageNumber) {
    String[] args = {voyageNumber};
    String sql = "select * from voyage where voyage_number = ?";
    try {
      return (VoyageReport) jdbc.queryForObject(sql, args, rowMapper);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  ReportingService() {
    // Needed by CGLIB
  }
  
}
