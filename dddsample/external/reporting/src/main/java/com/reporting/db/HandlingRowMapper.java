package com.reporting.db;

import static com.reporting.Constants.US_DATETIME;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import se.citerus.dddsample.reporting.api.Handling;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HandlingRowMapper implements ParameterizedRowMapper<Handling> {

  @Override
  public Handling mapRow(ResultSet rs, int rowNum) throws SQLException {
    Handling handling = new Handling();
    handling.setCompletedOn(US_DATETIME.format(rs.getTimestamp("completed_on")));
    handling.setLocation(rs.getString("location"));
    handling.setType(rs.getString("type"));
    handling.setVoyage(rs.getString("voyage_number"));
    return handling;
  }

}
