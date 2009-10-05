package com.reporting;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.reporting.Constants.*;

class VoyageReportRowMapper implements ParameterizedRowMapper<VoyageReport> {
  @Override
  public VoyageReport mapRow(ResultSet rs, int rowNum) throws SQLException {
    VoyageReport voyageReport = new VoyageReport();
    voyageReport.setVoyageNumber(rs.getString("voyage_number"));
    voyageReport.setCurrentStatus(rs.getString("current_status"));
    voyageReport.setDelayedByMinutes(rs.getInt("delayed_by_min"));
    voyageReport.setEtaNextStop(US_DATETIME.format(rs.getTimestamp("eta_next_stop")));
    voyageReport.setLastUpdatedOn(US_DATETIME.format(rs.getTimestamp("last_updated_on")));
    voyageReport.setNextStop(rs.getString("next_stop"));
    return voyageReport;
  }
}
