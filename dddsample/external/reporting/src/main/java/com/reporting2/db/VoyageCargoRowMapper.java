package com.reporting2.db;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import se.citerus.dddsample.reporting.api.OnboardCargo;

import java.sql.ResultSet;
import java.sql.SQLException;

public class VoyageCargoRowMapper implements ParameterizedRowMapper<OnboardCargo> {
  @Override
    public OnboardCargo mapRow(ResultSet rs, int rowNum) throws SQLException {
    OnboardCargo onboardCargo = new OnboardCargo();
    onboardCargo.setTrackingId(rs.getString("cargo_tracking_id"));
    onboardCargo.setFinalDestination(rs.getString("destination"));
    return onboardCargo;
  }
}
