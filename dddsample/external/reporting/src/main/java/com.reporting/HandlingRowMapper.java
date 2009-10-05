package com.reporting;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.reporting.Constants.*;

class HandlingRowMapper implements ParameterizedRowMapper<CargoReport.Handling> {

  @Override
  public CargoReport.Handling mapRow(ResultSet rs, int rowNum) throws SQLException {
    CargoReport.Handling handling = new CargoReport.Handling();
    handling.setCompletedOn(US_DATETIME.format(rs.getTimestamp("completed_on")));
    handling.setLocation(rs.getString("location"));
    handling.setType(rs.getString("type"));
    handling.setVoyage(rs.getString("voyage_number"));
    return handling;
  }

}
