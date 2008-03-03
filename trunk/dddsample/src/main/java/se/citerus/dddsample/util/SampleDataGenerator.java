package se.citerus.dddsample.util;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Timestamp;

/**
 * Provides sample data.
 */
public class SampleDataGenerator implements ServletContextListener {

  private static void loadHandlingEventData(JdbcTemplate jdbcTemplate) {
    String handlingEventSql =
      "insert into HandlingEvent (completionTime, registrationTime, type, location_id, carrierMovement_id, cargo_id) " +
      "values (?, ?, ?, ?, ?, ?)";
    Object[][] handlingEventArgs = {
        //XYZ (SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL)
        {ts(0),     ts((1)),    "RECEIVE",  1,  null,  1},
        {ts((10)),  ts((11)),   "LOAD",     1,  1,     1},
        {ts((20)),  ts((21)),   "UNLOAD",   5,  1,     1},
        {ts((30)),  ts((31)),   "LOAD",     5,  2,     1},
        {ts((40)),  ts((41)),   "UNLOAD",   6,  2,     1},
        {ts((50)),  ts((51)),   "LOAD",     6,  3,     1},
        {ts((60)),  ts((61)),   "UNLOAD",   3,  3,     1},
        {ts((70)),  ts((71)),   "LOAD",     3,  4,     1},
        {ts((80)),  ts((81)),   "UNLOAD",   4,  4,     1},
        {ts((90)),  ts((91)),   "LOAD",     4,  5,     1},
        {ts((100)), ts((101)),  "UNLOAD",   2,  5,     1},
        {ts((110)), ts((111)),  "CLAIM",    2,  null,  1},

        //ZYX (AUMEL - USCHI - DEHAM -)
        {ts((0)),   ts((1)),    "RECEIVE",  2,  null,  3},
        {ts((10)),  ts((11)),   "LOAD",     2,  7,     3},
        {ts((20)),  ts((21)),   "UNLOAD",   7,  7,     3},
        {ts((30)),  ts((31)),   "LOAD",     7,  8,     3},
        {ts((40)),  ts((41)),   "UNLOAD",   6,  8,     3},
        {ts((50)),  ts((51)),   "LOAD",     6,  9,     3},

        //ABC
        {ts((20)),  ts((21)),   "CLAIM",    2,  null,  2}

        //CBA
    };
    executeUpdate(jdbcTemplate, handlingEventSql, handlingEventArgs);
  }

  private static void loadCarrierMovementData(JdbcTemplate jdbcTemplate) {
    String carrierMovementSql = "insert into CarrierMovement (id, carrier_movement_id, from_id, to_id) values (?,?,?,?)";
    Object[][] carrierMovementArgs = {
     // SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL
      {1, "CAR_001",1,5},
      {2, "CAR_002",5,6},
      {3, "CAR_003",6,3},
      {4, "CAR_004",3,4},
      {5, "CAR_005",4,2},

      // FIHEL - SESTO
      {6, "CAR_006",5,1},

      // AUMEL - USCHI - DEHAM - SESTO
      {7, "CAR_007",2,7},
      {8, "CAR_008",7,6},
      {9, "CAR_009",6,1}
    };
    executeUpdate(jdbcTemplate, carrierMovementSql, carrierMovementArgs);
  }

  private static void loadCargoData(JdbcTemplate jdbcTemplate) {
    String cargoSql = "insert into Cargo (id, tracking_id, origin_id, finalDestination_id) values (?, ?, ?, ?)";
    Object[][] cargoArgs = {
      {1, "XYZ",1,2},
      {2, "ABC",1,5},
      {3, "ZYX",2,1},
      {4, "CBA",5,1}
    };
    executeUpdate(jdbcTemplate, cargoSql, cargoArgs);
  }

  private static void loadLocationData(JdbcTemplate jdbcTemplate) {
    String locationSql = "insert into Location (id, unlocode, name) values (?, ?, ?)";
    Object[][] locationArgs = {
      {1, "SESTO", "Stockholm"},
      {2, "AUMEL", "Melbourne"},
      {3, "CNHKG", "Hongkong"},
      {4, "JPTOK", "Tokyo"},
      {5, "FIHEL", "Helsinki"},
      {6, "DEHAM", "Hamburg"},
      {7, "USCHI", "Chicago"}
    };
    executeUpdate(jdbcTemplate, locationSql, locationArgs);
  }

  public void contextInitialized(ServletContextEvent event) {
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
    DataSource dataSource = (DataSource) BeanFactoryUtils.beanOfType(context, DataSource.class);
    PlatformTransactionManager transactionManager = (PlatformTransactionManager) BeanFactoryUtils.beanOfType(context, PlatformTransactionManager.class);
    loadSampleData(new JdbcTemplate(dataSource), new TransactionTemplate(transactionManager));
  }

  public void contextDestroyed(ServletContextEvent event) {}

  public static void loadSampleData(final JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        loadLocationData(jdbcTemplate);
        loadCargoData(jdbcTemplate);
        loadCarrierMovementData(jdbcTemplate);
        loadHandlingEventData(jdbcTemplate);
      }
    });
  }

  private static void executeUpdate(JdbcTemplate jdbcTemplate, String sql, Object[][] args) {
    for (Object[] arg : args) {
      jdbcTemplate.update(sql, arg);
    }
  }

  private static Timestamp ts(int time) {
    return new Timestamp(time);
  }

}
