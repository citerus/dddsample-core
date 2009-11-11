package com.reporting2.db;

import static com.reporting2.Constants.US_DATETIME;
import com.reporting2.reports.CargoReport;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import se.citerus.dddsample.reporting.api.CargoDetails;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CargoReportRowMapper implements ParameterizedRowMapper<CargoReport> {

  @Override
  public CargoReport mapRow(ResultSet rs, int rowNum) throws SQLException {
    CargoReport cargoReport = new CargoReport();
    CargoDetails cargoDetails = new CargoDetails();
    cargoDetails.setTrackingId(rs.getString("cargo_tracking_id"));
    cargoDetails.setArrivalDeadline(US_DATETIME.format(rs.getTimestamp("arrival_deadline")));
    cargoDetails.setCurrentLocation(rs.getString("current_location"));
    cargoDetails.setCurrentStatus(rs.getString("current_status"));
    cargoDetails.setCurrentVoyage(rs.getString("current_voyage_number"));
    cargoDetails.setEta(US_DATETIME.format(rs.getTimestamp("eta")));
    cargoDetails.setFinalDestination(rs.getString("destination"));
    cargoDetails.setLastUpdatedOn(US_DATETIME.format(rs.getTimestamp("last_updated_on")));
    cargoDetails.setReceivedIn(rs.getString("received_in"));
    cargoReport.setCargo(cargoDetails);
    return cargoReport;
  }

}
