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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides sample data.
 */
public class SampleDataGenerator implements ServletContextListener {

  private static final Timestamp base; 
  static {
    try {
      Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2008-01-01");
      base = new Timestamp(date.getTime() - 1000L * 60 * 60 * 24 * 100);
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  private static void loadHandlingEventData(JdbcTemplate jdbcTemplate) {
    String handlingEventSql =
      "insert into HandlingEvent (completionTime, registrationTime, type, location_id, carrierMovement_id, cargo_id) " +
      "values (?, ?, ?, ?, ?, ?)";

    Object[][] handlingEventArgs = {
        //XYZ (SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL)
        {ts(0),     ts((0)),    "RECEIVE",  1,  null,  1},
        {ts((4)),   ts((5)),    "LOAD",     1,  1,     1},
        {ts((14)),  ts((14)),   "UNLOAD",   5,  1,     1},
        {ts((15)),  ts((15)),   "LOAD",     5,  2,     1},
        {ts((30)),  ts((30)),   "UNLOAD",   6,  2,     1},
        {ts((33)),  ts((33)),   "LOAD",     6,  3,     1},
        {ts((34)),  ts((34)),   "UNLOAD",   3,  3,     1},
        {ts((60)),  ts((60)),   "LOAD",     3,  4,     1},
        {ts((70)),  ts((71)),   "UNLOAD",   4,  4,     1},
        {ts((75)),  ts((75)),   "LOAD",     4,  5,     1},
        {ts((88)),  ts((88)),   "UNLOAD",   2,  5,     1},
        {ts((100)), ts((102)),  "CLAIM",    2,  null,  1},

        //ZYX (AUMEL - USCHI - DEHAM -)
        {ts((200)),   ts((201)),  "RECEIVE",  2,  null,  3},
        {ts((202)),   ts((202)),  "LOAD",     2,  7,     3},
        {ts((208)),   ts((208)),  "UNLOAD",   7,  7,     3},
        {ts((212)),   ts((212)),  "LOAD",     7,  8,     3},
        {ts((230)),   ts((230)),  "UNLOAD",   6,  8,     3},
        {ts((235)),   ts((235)),  "LOAD",     6,  9,     3},

        //ABC
        {ts((20)),  ts((21)),   "CLAIM",    2,  null,  2},

        //CBA
        {ts((0)),   ts((1)),    "RECEIVE",  2,  null,  4},
        {ts((10)),  ts((11)),   "LOAD",     2,  7,     4},
        {ts((20)),  ts((21)),   "UNLOAD",   7,  7,     4},

        //FGH
        {ts(100),   ts(160),    "RECEIVE",  3,  null,   5},
        {ts(150),   ts(110),    "LOAD",     3,  10,     5},

        // JKL
        {ts(200),   ts(220),    "RECEIVE",  6,  null,   6},
        {ts(300),   ts(330),    "LOAD",     6,  12,     6},
        {ts(400),   ts(440),    "UNLOAD",   5,  12,     6}  // Unexpected event
    };
    executeUpdate(jdbcTemplate, handlingEventSql, handlingEventArgs);
  }

  private static void loadCarrierMovementData(JdbcTemplate jdbcTemplate) {
    String carrierMovementSql =
      "insert into CarrierMovement (id, carrier_movement_id, from_id, to_id) " +
      "values (?,?,?,?)";

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
      {9, "CAR_009",6,1},

      // CNHKG - AUMEL
      {10,"CAR_010",3,2},
      // AUMEL - FIHEL
      {11,"CAR_011",2,5},
      // DEHAM - SESTO
      {12,"CAR_020",6,1},
      // SESTO - USCHI
      {13,"CAR_021",1,7},
      // USCHI - JPTKO
      {14,"CAR_022",7,4}
    };
    executeUpdate(jdbcTemplate, carrierMovementSql, carrierMovementArgs);
  }

  private static void loadCargoData(JdbcTemplate jdbcTemplate) {
    String cargoSql =
      "insert into Cargo (id, tracking_id, origin_id, destination_id, itinerary_id) " +
      "values (?, ?, ?, ?, ?)";

    Object[][] cargoArgs = {
      {1, "XYZ", 1, 2, null},
      {2, "ABC", 1, 5, null},
      {3, "ZYX", 2, 1, null},
      {4, "CBA", 5, 1, null},
      {5, "FGH", 3, 5, 1},
      {6, "JKL", 6, 4, 2}
    };
    executeUpdate(jdbcTemplate, cargoSql, cargoArgs);
  }

  private static void loadLocationData(JdbcTemplate jdbcTemplate) {
    String locationSql =
      "insert into Location (id, unlocode, name) " +
      "values (?, ?, ?)";

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

  private static void loadItineraryData(JdbcTemplate jdbcTemplate) {
    String itinerarySql = "insert into Itinerary (id) values (?)";

    Object[][] itineraryArgs = {
      {1},
      {2}
    };
    executeUpdate(jdbcTemplate, itinerarySql, itineraryArgs);

    String legSql =
      "insert into Leg (id, itinerary_id, carrierMovement_id, from_id, to_id) " +
      "values (?,?,?,?,?)";

    Object [][] legArgs = {
      // Cargo 5: Hongkong - Melbourne - Stockholm - Helsinki
      {1,1,10,3,2},
      {2,1,11,2,1},
      {3,1,11,1,5},
      // Cargo 6: Hamburg - Stockholm - Chicago - Tokyo
      {4,2,12,6,1},
      {5,2,13,1,7},
      {6,2,14,7,4}
    };
    executeUpdate(jdbcTemplate, legSql, legArgs);
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
        loadCarrierMovementData(jdbcTemplate);
        loadItineraryData(jdbcTemplate);
        loadCargoData(jdbcTemplate);
        loadHandlingEventData(jdbcTemplate);
      }
    });
  }

  private static void executeUpdate(JdbcTemplate jdbcTemplate, String sql, Object[][] args) {
    for (Object[] arg : args) {
      jdbcTemplate.update(sql, arg);
    }
  }

  private static Timestamp ts(int hours) {
    return new Timestamp(base.getTime() + 1000L * 60 * 60 * hours);
  }

  public static Date offset(int hours) {
    return new Date(ts(hours).getTime());
  }
}
