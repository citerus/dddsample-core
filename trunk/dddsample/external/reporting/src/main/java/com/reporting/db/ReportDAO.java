package com.reporting.db;

import com.reporting.reports.CargoReport;
import com.reporting.reports.VoyageReport;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import se.citerus.dddsample.reporting.api.CargoDetails;
import se.citerus.dddsample.reporting.api.Handling;
import se.citerus.dddsample.reporting.api.OnboardCargo;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
  
  private SimpleJdbcTemplate jdbc;
  private HandlingRowMapper handlingRowMapper;
  private CargoReportRowMapper cargoReportRowMapper;
  private VoyageReportRowMapper rowMapper;
  private VoyageCargoRowMapper voyageCargoRowMapper;

  public ReportDAO(DataSource dataSource) {
    this.jdbc = new SimpleJdbcTemplate(dataSource);
    this.handlingRowMapper = new HandlingRowMapper();
    this.cargoReportRowMapper = new CargoReportRowMapper();
    this.rowMapper = new VoyageReportRowMapper();
    this.voyageCargoRowMapper = new VoyageCargoRowMapper();
  }

  public CargoReport loadCargoReport(String trackingId) {
    try {
      String[] args = {trackingId};
      String sql = "select * from cargo where cargo_tracking_id = ?";
      CargoReport cargoReport = (CargoReport) jdbc.getJdbcOperations().queryForObject(sql, args, cargoReportRowMapper);
      cargoReport.setHandlings(loadHandlings(trackingId));
      return cargoReport;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public VoyageReport loadVoyageReport(String voyageNumber) {
    String[] args = {voyageNumber};
    String sql = "select * from voyage where voyage_number = ?";
    try {
      VoyageReport voyageReport = (VoyageReport) jdbc.getJdbcOperations().queryForObject(sql, args, rowMapper);
      voyageReport.setOnboardCargos(loadOnboardCargos(voyageNumber));
      return voyageReport;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  public void storeCargoDetals(CargoDetails cargoDetails) {
    int count = jdbc.queryForInt("select count(*) from cargo where cargo_tracking_id = ?", cargoDetails.getTrackingId());

    String sql;
    Object[] params;
    if (count > 0) {
      sql =
        "update cargo set " +
          "received_in = ?," +
          "destination = ?," +
          "arrival_deadline = ?," +
          "eta = ?," +
          "current_status = ?," +
          "current_voyage_number = ?," +
          "current_location = ?," +
          "last_updated_on = ? " +
        "where cargo_tracking_id = ?";
      params = new Object[] {
        cargoDetails.getReceivedIn(),
        cargoDetails.getFinalDestination(),
        cargoDetails.getArrivalDeadline(),
        cargoDetails.getEta(),
        cargoDetails.getCurrentStatus(),
        cargoDetails.getCurrentVoyage(),
        cargoDetails.getCurrentLocation(),
        cargoDetails.getLastUpdatedOn(),
        cargoDetails.getTrackingId()
      };
    } else {
      sql =
        "insert into cargo (" +
          "cargo_tracking_id," +
          "received_in," +
          "destination," +
          "arrival_deadline," +
          "eta," +
          "current_status," +
          "current_voyage_number," +
          "current_location," +
          "last_updated_on) " +
        "values (?,?,?,?,?,?,?,?,?)";
      params = new Object[] {
        cargoDetails.getTrackingId(),
        cargoDetails.getReceivedIn(),
        cargoDetails.getFinalDestination(),
        cargoDetails.getArrivalDeadline(),
        cargoDetails.getEta(),
        cargoDetails.getCurrentStatus(),
        cargoDetails.getCurrentVoyage(),
        cargoDetails.getCurrentLocation(),
        cargoDetails.getLastUpdatedOn()
      };
    }

    jdbc.update(sql, params);
  }

  public void storeHandling(String trackingId, Handling handling) {
    String sql = "insert into handling (cargo_tracking_id,type,location,voyage_number,completed_on) values (?,?,?,?,?)";
    jdbc.update(sql, trackingId, handling.getType(), handling.getLocation(), handling.getVoyage(), handling.getCompletedOn());  
  }

  private List<Handling> loadHandlings(String trackingId) {
    final List<Handling> handlings = new ArrayList<Handling>();
    RowCallbackHandler handler = new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        handlings.add(handlingRowMapper.mapRow(rs, rs.getRow()));
      }
    };
    String[] args = {trackingId};
    String sql = "select * from handling where cargo_tracking_id = ?";
    jdbc.getJdbcOperations().query(sql, args, handler);
    return handlings;
  }

  private List<OnboardCargo> loadOnboardCargos(String voyageNumber) {
    final List<OnboardCargo> onboardCargos = new ArrayList<OnboardCargo>();
    String[] args = {voyageNumber};
    String sql = "select cargo_tracking_id, destination from cargo where current_voyage_number = ?";
    jdbc.getJdbcOperations().query(sql, args, new RowCallbackHandler() {
      @Override
      public void processRow(ResultSet rs) throws SQLException {
        onboardCargos.add(voyageCargoRowMapper.mapRow(rs, rs.getRow()));
      }
    });

    return onboardCargos;
  }

  ReportDAO() {
  }
}
