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
        //XYZ (SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL)
        {UUID.randomUUID().toString().getBytes(), new Timestamp(0), new Timestamp(1), "RECEIVE", 1, null, "XYZ"},  
        {UUID.randomUUID().toString().getBytes(), new Timestamp(10), new Timestamp(11), "LOAD", 1, "CAR_001", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(20), new Timestamp(21), "UNLOAD", 5, "CAR_001", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(30), new Timestamp(31), "LOAD", 5, "CAR_002", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(40), new Timestamp(41), "UNLOAD", 6, "CAR_002", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(50), new Timestamp(51), "LOAD", 6, "CAR_003", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(60), new Timestamp(61), "UNLOAD", 3, "CAR_003", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(70), new Timestamp(71), "LOAD", 3, "CAR_004", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(80), new Timestamp(81), "UNLOAD", 4, "CAR_004", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(90), new Timestamp(91), "LOAD", 4, "CAR_005", "XYZ"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(100), new Timestamp(101), "UNLOAD", 2, "CAR_005", "XYZ"},        
        {UUID.randomUUID().toString().getBytes(), new Timestamp(110), new Timestamp(111), "CLAIM", 2, null, "XYZ"},
            
        //ZYX (AUMEL - USCHI - DEHAM -)
        {UUID.randomUUID().toString().getBytes(), new Timestamp(0), new Timestamp(1), "RECEIVE", 2, null, "ZYX"},  
        {UUID.randomUUID().toString().getBytes(), new Timestamp(10), new Timestamp(11), "LOAD", 2, "CAR_007", "ZYX"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(20), new Timestamp(21), "UNLOAD", 7, "CAR_007", "ZYX"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(30), new Timestamp(31), "LOAD", 7, "CAR_008", "ZYX"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(40), new Timestamp(41), "UNLOAD", 6, "CAR_008", "ZYX"},
        {UUID.randomUUID().toString().getBytes(), new Timestamp(50), new Timestamp(51), "LOAD", 6, "CAR_009", "ZYX"},
        
        //ABC
        {UUID.randomUUID().toString().getBytes(), new Timestamp(20), new Timestamp(21), "CLAIM", 2, null, "ABC"}
        
        //CBA
    };
    for (Object[] handlingEventArg : handlingEventArgs) {
      jdbcTemplate.update(handlingEventSql, handlingEventArg);
    }
  }

  private static void loadCarrierMovementData(JdbcTemplate jdbcTemplate) {
    String carrierMovementSql = "insert into CarrierMovement (id, from_id, to_id) values (?,?,?)";
    Object[][] carrierMovementArgs = {
     // SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL
      {"CAR_001",1,5}, 
      {"CAR_002",5,6},
      {"CAR_003",6,3},
      {"CAR_004",3,4},
      {"CAR_005",4,2},
      
      // FIHEL - SESTO
      {"CAR_006",5,1},
      
      // AUMEL - USCHI - DEHAM - SESTO
      {"CAR_007",2,7}, 
      {"CAR_008",7,6},
      {"CAR_009",6,1}
    };
    for (Object[] carrierMovementArg : carrierMovementArgs) {
      jdbcTemplate.update(carrierMovementSql, carrierMovementArg);
    }
  }

  private static void loadCargoData(JdbcTemplate jdbcTemplate) {
    String cargoSql = "insert into Cargo (id, origin_id, finalDestination_id) values (?, ?, ?)";
    Object[][] cargoArgs = {
      {"XYZ",1,2},
      {"ABC",1,5},
      {"ZYX",2,1},
      {"CBA",5,1}
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
      {3L, "CNHKG"},
      {4L, "JPTOK"},
      {5L, "FIHEL"},
      {6L, "DEHAM"},
      {7L, "USCHI"}
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
