package com.reporting;

import static com.reporting.Constants.US_DATETIME;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Produces({"application/json", "application/pdf"})
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
  public Response getCargoReport(@PathParam("trackingId") String trackingId) throws ParseException {
    CargoReport cargoReport = loadCargoReport(trackingId);
    if (cargoReport == null) return null;

    cargoReport.setHandlings(loadHandlings(trackingId));

    return Response.ok(cargoReport).
      lastModified(US_DATETIME.parse(cargoReport.getLastUpdatedOn())).
      build();
  }

  @GET
  @Path("/voyage/{voyageNumber}")
  @Transactional
  public Response getVoyageReport(@PathParam("voyageNumber") String voyageNumber) throws ParseException {
    VoyageReport voyageReport = loadVoyageReport(voyageNumber);
    if (voyageReport == null) return null;

    voyageReport.setOnboardCargos(loadOnboardCargos(voyageNumber));

    return Response.ok(voyageReport).
      lastModified(US_DATETIME.parse(voyageReport.getLastUpdatedOn())).
      build();
  }

  // --- Private methods ---

  private CargoReport loadCargoReport(String trackingId) {
    try {
      String[] args = {trackingId};
      String sql = "select * from cargo where cargo_tracking_id = ?";
      return (CargoReport) jdbc.queryForObject(sql, args, cargoReportRowMapper);
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

  private List<VoyageReport.Cargo> loadOnboardCargos(String voyageNumber) {
    final List<VoyageReport.Cargo> cargos = new ArrayList<VoyageReport.Cargo>();
    final ParameterizedRowMapper<VoyageReport.Cargo> rowMapper = new ParameterizedRowMapper<VoyageReport.Cargo>() {
      @Override
      public VoyageReport.Cargo mapRow(ResultSet rs, int rowNum) throws SQLException {
        VoyageReport.Cargo cargo = new VoyageReport.Cargo();
        cargo.setTrackingId(rs.getString("cargo_tracking_id"));
        cargo.setFinalDestination(rs.getString("destination"));
        return cargo;
      }
    };
    String[] args = {voyageNumber};
    String sql = "select cargo_tracking_id, destination from cargo where current_voyage_number = ?";
    jdbc.query(sql, args, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        cargos.add(rowMapper.mapRow(rs, rs.getRow()));
      }
    });

    return cargos;
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
