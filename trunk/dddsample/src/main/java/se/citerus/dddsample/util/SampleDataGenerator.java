package se.citerus.dddsample.util;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Provides sample data.
 */
public class SampleDataGenerator implements ServletContextListener {

  public static void loadSampleData(JdbcTemplate jdbcTemplate) {
    loadLocationData(jdbcTemplate);
    loadCargoData(jdbcTemplate);
    loadCarrierMovementData(jdbcTemplate);
    loadHandlingEventData(jdbcTemplate);
  }

  private static void loadHandlingEventData(JdbcTemplate jdbcTemplate) {
    String handlingEventSql =
      "insert into HandlingEvent (id, completionTime, registrationTime, type, location_id, carrierMovement_id, cargo_id) " +
      "values (?, ?, ?, ?, ?, ?, ?)";
    Object[][] handlingEventArgs = {
            {UUID.randomUUID().toString().getBytes(), new Timestamp(10), new Timestamp(11), "CLAIM", 1, null, "XYZ"},
            {UUID.randomUUID().toString().getBytes(), new Timestamp(20), new Timestamp(21), "CLAIM", 2, null, "ABC"}
    };
    for (Object[] handlingEventArg : handlingEventArgs) {
      jdbcTemplate.update(handlingEventSql, handlingEventArg);
    }
  }

  private static void loadCarrierMovementData(JdbcTemplate jdbcTemplate) {
    String carrierMovementSql = "insert into CarrierMovement (id, from_id, to_id) values (?,?,?)";
    Object[][] carrierMovementArgs = {
      {"CAR_001",1,2},
      {"CAR_002",2,3},
      {"CAR_003",3,1}
    };
    for (Object[] carrierMovementArg : carrierMovementArgs) {
      jdbcTemplate.update(carrierMovementSql, carrierMovementArg);
    }
  }

  private static void loadCargoData(JdbcTemplate jdbcTemplate) {
    String cargoSql = "insert into Cargo (id, origin_id, finalDestination_id) values (?, ?, ?)";
    Object[][] cargoArgs = {
      {"XYZ",1,2},
      {"ABC",3,1},
      {"ZYX",2,3}
    };
    for (Object[] cargoArg : cargoArgs) {
      jdbcTemplate.update(cargoSql, cargoArg);
    }
  }

  private static void loadLocationData(JdbcTemplate jdbcTemplate) {
    String locationSql = "insert into Location (id, unlocode) values (?, ?)";
    Object[][] locationArgs = {
      {1L, "SESTO"},
      {2L, "AUMEL"},
      {3L, "CNHKG"}
    };
    for (Object[] locationArg : locationArgs) {
      jdbcTemplate.update(locationSql, locationArg);
    }
  }

  public void contextInitialized(ServletContextEvent event) {
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
    DataSource dataSource = (DataSource) BeanFactoryUtils.beanOfType(context, DataSource.class);
    loadSampleData(new JdbcTemplate(dataSource));
  }

  public void contextDestroyed(ServletContextEvent event) {}

}
