package com.reporting;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

class CargoReportRowMapper implements ParameterizedRowMapper<CargoReport> {

  @Override
  public CargoReport mapRow(ResultSet rs, int rowNum) throws SQLException {
    CargoReport cargoReport = new CargoReport();
    cargoReport.setTrackingId(rs.getString("cargo_tracking_id"));
    cargoReport.setArrivalDeadline(Constants.US_DATETIME.format(rs.getTimestamp("arrival_deadline")));
    cargoReport.setCurrentLocation(rs.getString("current_location"));
    cargoReport.setCurrentStatus(rs.getString("current_status"));
    cargoReport.setCurrentVoyage(rs.getString("current_voyage_number"));
    cargoReport.setEta(Constants.US_DATETIME.format(rs.getTimestamp("eta")));
    cargoReport.setFinalDestination(rs.getString("destination"));
    cargoReport.setLastUpdatedOn(Constants.US_DATETIME.format(rs.getTimestamp("last_updated_on")));
    cargoReport.setReceivedIn(rs.getString("received_in"));
    return cargoReport;
  }

}
