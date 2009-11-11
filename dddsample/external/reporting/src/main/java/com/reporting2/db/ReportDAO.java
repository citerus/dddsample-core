package com.reporting2.db;

import com.reporting2.reports.CargoReport;
import com.reporting2.reports.VoyageReport;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.reporting.api.Handling;
import se.citerus.dddsample.reporting.api.OnboardCargo;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReportDAO {
  
  private JdbcTemplate jdbc;
  private HandlingRowMapper handlingRowMapper;
  private CargoReportRowMapper cargoReportRowMapper;
  private VoyageReportRowMapper rowMapper;
  private VoyageCargoRowMapper voyageCargoRowMapper;

  public ReportDAO(DataSource dataSource) {
    this.jdbc = new JdbcTemplate(dataSource);
    this.handlingRowMapper = new HandlingRowMapper();
    this.cargoReportRowMapper = new CargoReportRowMapper();
    this.rowMapper = new VoyageReportRowMapper();
    this.voyageCargoRowMapper = new VoyageCargoRowMapper();
  }

  @Transactional(readOnly = true)
  public CargoReport loadCargoReport(String trackingId) {
    try {
      String[] args = {trackingId};
      String sql = "select * from cargo where cargo_tracking_id = ?";
      CargoReport cargoReport = (CargoReport) jdbc.queryForObject(sql, args, cargoReportRowMapper);
      cargoReport.setHandlings(loadHandlings(trackingId));
      return cargoReport;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Transactional(readOnly = true)
  public VoyageReport loadVoyageReport(String voyageNumber) {
    String[] args = {voyageNumber};
    String sql = "select * from voyage where voyage_number = ?";
    try {
      VoyageReport voyageReport = (VoyageReport) jdbc.queryForObject(sql, args, rowMapper);
      voyageReport.setOnboardCargos(loadOnboardCargos(voyageNumber));
      return voyageReport;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
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
    jdbc.query(sql, args, handler);
    return handlings;
  }

  private List<OnboardCargo> loadOnboardCargos(String voyageNumber) {
    final List<OnboardCargo> onboardCargos = new ArrayList<OnboardCargo>();
    String[] args = {voyageNumber};
    String sql = "select cargo_tracking_id, destination from cargo where current_voyage_number = ?";
    jdbc.query(sql, args, new RowCallbackHandler() {
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
