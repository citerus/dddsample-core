package se.citerus.dddsample.tracking.core.application.util;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.handling.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.util.Arrays.asList;
import static se.citerus.dddsample.tracking.core.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.*;

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
    String activitySql = "insert into HandlingActivity (id, handling_event_type, location_id, voyage_id) values (?, ?, ?, ?)";
      Object[][] actvityArgs = {
        {1, "RECEIVE", 1, null},
        {2, "LOAD",    1, 1},
        {3, "UNLOAD",  5, 1},
        {4, "LOAD",    5, 1},
        {5, "UNLOAD",  6, 1},
        {6, "LOAD",    6, 1},
        {7, "UNLOAD",  3, 1},
        {8, "LOAD",    3, 1},
        {9, "UNLOAD",  4, 1},
        {10, "LOAD",    4, 1},
        {11, "UNLOAD",  2, 1},
        {12, "CLAIM",   2, null},

        //ZYX (AUMEL - USCHI - DEHAM -)
        {13, "RECEIVE", 2, null},
        {14, "LOAD",    2, 2},
        {15, "UNLOAD",  7, 2},
        {16, "LOAD",    7, 2},
        {17, "UNLOAD",  6, 2},
        {18, "LOAD",    6, 2},

        //ABC
        {19, "CLAIM", 2, null},

        //CBA
        {20, "RECEIVE", 2, null},
        {21, "LOAD",    2, 2},
        {22, "UNLOAD",  7, 2},

        //FGH
        {23, "RECEIVE", 3, null},
        {24, "LOAD",    3, 3},

        // JKL
        {25, "RECEIVE", 6, null},
        {26, "LOAD",    6, 3},
        {27, "UNLOAD",  5, 3}  // Unexpected event
      };
    executeUpdate(jdbcTemplate, activitySql, actvityArgs);


    String handlingEventSql =
      "insert into HandlingEvent (sequence_number, completionTime, registrationTime, cargo_id, operator_code, activity_id) " +
        "values (?, ?, ?, ?, ?, ?)";

    Object[][] handlingEventArgs = {
      //XYZ (SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL)
      {1, ts(0), ts((0)), 1, null, 1},
      {2, ts((4)), ts((5)), 1, "AS34F", 2},
      {3, ts((14)), ts((14)), 1, "AS34F", 3},
      {4, ts((15)), ts((15)), 1, "AS34F", 4},
      {5, ts((30)), ts((30)), 1, "AS34F", 5},
      {6, ts((33)), ts((33)), 1, "AS34F", 6},
      {7, ts((34)), ts((34)), 1, "AS34F", 7},
      {8, ts((60)), ts((60)), 1, "AS34F", 8},
      {9, ts((70)), ts((71)), 1, "AS34F", 9},
      {10, ts((75)), ts((75)), 1, "AS34F", 10},
      {11, ts((88)), ts((88)), 1, "AS34F", 11},
      {12, ts((100)), ts((102)), 1, null, 12},

      //ZYX (AUMEL - USCHI - DEHAM -)
      {13, ts((200)), ts((201)), 3, null, 13},
      {14, ts((202)), ts((202)), 3, "AS34F", 14},
      {15, ts((208)), ts((208)), 3, "AS34F", 15},
      {16, ts((212)), ts((212)), 3, "AS34F", 16},
      {17, ts((230)), ts((230)), 3, "AS34F", 17},
      {18, ts((235)), ts((235)), 3, "AS34F", 18},

      //ABC
      {19, ts((20)), ts((21)), 2, null, 19},

      //CBA
      {20, ts((0)), ts((1)), 4, null, 20},
      {21, ts((10)), ts((11)), 4, "AS34F", 21},
      {22, ts((20)), ts((21)), 4, "AS34F", 22},

      //FGH
      {23, ts(100), ts(160), 5, null, 23},
      {24, ts(150), ts(110), 5, "AS34F", 24},

      // JKL
      {25, ts(200), ts(220), 6, null, 25},
      {26, ts(300), ts(330), 6, "AS34F", 26},
      {27, ts(400), ts(440), 6, null, 27}  // Unexpected event
    };
    executeUpdate(jdbcTemplate, handlingEventSql, handlingEventArgs);
  }

  private static void loadCarrierMovementData(JdbcTemplate jdbcTemplate) {
    String voyageSql =
      "insert into Voyage (id, voyage_number) values (?, ?)";
    Object[][] voyageArgs = {
      {1, "0101"},
      {2, "0202"},
      {3, "0303"}
    };
    executeUpdate(jdbcTemplate, voyageSql, voyageArgs);

    String carrierMovementSql =
      "insert into CarrierMovement (id, voyage_id, departure_location_id, arrival_location_id, departure_time, arrival_time, cm_index) " +
        "values (?,?,?,?,?,?,?)";

    Object[][] carrierMovementArgs = {
      // SESTO - FIHEL - DEHAM - CNHKG - JPTOK - AUMEL (voyage 0101)
      {1, 1, 1, 5, ts(1), ts(2), 0},
      {2, 1, 5, 6, ts(1), ts(2), 1},
      {3, 1, 6, 3, ts(1), ts(2), 2},
      {4, 1, 3, 4, ts(1), ts(2), 3},
      {5, 1, 4, 2, ts(1), ts(2), 4},

      // AUMEL - USCHI - DEHAM - SESTO - FIHEL (voyage 0202)
      {7, 2, 2, 7, ts(1), ts(2), 0},
      {8, 2, 7, 6, ts(1), ts(2), 1},
      {9, 2, 6, 1, ts(1), ts(2), 2},
      {6, 2, 1, 5, ts(1), ts(2), 3},

      // CNHKG - AUMEL - FIHEL - DEHAM - SESTO - USCHI - JPTKO (voyage 0303)
      {10, 3, 3, 2, ts(1), ts(2), 0},
      {11, 3, 2, 5, ts(1), ts(2), 1},
      {12, 3, 6, 1, ts(1), ts(2), 2},
      {13, 3, 1, 7, ts(1), ts(2), 3},
      {14, 3, 7, 4, ts(1), ts(2), 4}
    };
    executeUpdate(jdbcTemplate, carrierMovementSql, carrierMovementArgs);
  }

  private static void loadCargoData(JdbcTemplate jdbcTemplate) {
    String cargoSql =
      "insert into Cargo (id, tracking_id, spec_origin_id, spec_destination_id, spec_arrival_deadline, last_update) " +
        "values (?, ?, ?, ?, ?, ?)";

    Object[][] cargoArgs = {
      {1, "XYZ", 1, 2, ts(10), ts(100)},
      {2, "ABC", 1, 5, ts(20), ts(100)},
      {3, "ZYX", 2, 1, ts(30), ts(100)},
      {4, "CBA", 5, 1, ts(40), ts(100)},
      {5, "FGH", 3, 5, ts(50), ts(100)},
      {6, "JKL", 6, 4, ts(60), ts(100)}
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
    String legSql =
      "insert into Leg (id, cargo_id, voyage_id, load_location_id, unload_location_id, load_time, unload_time, leg_index) " +
        "values (?,?,?,?,?,?,?,?)";

    Object[][] legArgs = {
      // Cargo 5: Hongkong - Melbourne - Stockholm - Helsinki
      {1, 5, 1, 3, 2, ts(1), ts(2), 0},
      {2, 5, 1, 2, 1, ts(3), ts(4), 1},
      {3, 5, 1, 1, 5, ts(4), ts(5), 2},
      // Cargo 6: Hamburg - Stockholm - Chicago - Tokyo
      {4, 6, 2, 6, 1, ts(1), ts(2), 0},
      {5, 6, 2, 1, 7, ts(3), ts(4), 1},
      {6, 6, 2, 7, 4, ts(5), ts(6), 2}
    };
    executeUpdate(jdbcTemplate, legSql, legArgs);
  }

  public void contextInitialized(ServletContextEvent event) {
    WebApplicationContext context = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
    PlatformTransactionManager transactionManager = (PlatformTransactionManager) BeanFactoryUtils.beanOfType(context, PlatformTransactionManager.class);
    TransactionTemplate tt = new TransactionTemplate(transactionManager);
    //loadSampleData(new JdbcTemplate(dataSource), tt);


    SessionFactory sf = (SessionFactory) BeanFactoryUtils.beanOfType(context, SessionFactory.class);
    HandlingEventFactory handlingEventFactory = new HandlingEventFactory(
      getBean(context, CargoRepository.class),
      getBean(context, VoyageRepository.class),
      getBean(context, LocationRepository.class));
    loadHibernateData(tt, sf, handlingEventFactory, getBean(context, HandlingEventRepository.class));
  }

  private <T> T getBean(WebApplicationContext context, Class<T> cls) {
    //noinspection unchecked
    return (T) BeanFactoryUtils.beanOfType(context, cls);
  }

  public static void loadHibernateData(TransactionTemplate tt, final SessionFactory sf, final HandlingEventFactory handlingEventFactory, final HandlingEventRepository handlingEventRepository) {
    tt.execute(new TransactionCallbackWithoutResult() {
      @Override
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        Session session = sf.getCurrentSession();

        for (Location location : SampleLocations.getAll()) {
          session.save(location);
        }

        session.save(HONGKONG_TO_NEW_YORK);
        session.save(NEW_YORK_TO_DALLAS);
        session.save(DALLAS_TO_HELSINKI);
        session.save(HELSINKI_TO_HONGKONG);
        session.save(DALLAS_TO_HELSINKI_ALT);

        RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, toDate("2009-03-15"));
        TrackingId trackingId = new TrackingId("ABC123");
        Cargo abc123 = new Cargo(trackingId, routeSpecification);

        Itinerary itinerary = new Itinerary(asList(
          new Leg(HONGKONG_TO_NEW_YORK, HONGKONG, NEWYORK, toDate("2009-03-02"), toDate("2009-03-05")),
          new Leg(NEW_YORK_TO_DALLAS, NEWYORK, DALLAS, toDate("2009-03-06"), toDate("2009-03-08")),
          new Leg(DALLAS_TO_HELSINKI, DALLAS, HELSINKI, toDate("2009-03-09"), toDate("2009-03-12"))
        ));
        abc123.assignToRoute(itinerary);

        session.save(abc123);

        try {
          HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-01"), trackingId, null, HONGKONG.unLocode(), HandlingActivityType.RECEIVE, new OperatorCode("ABCDE")
          );
          session.save(event1);

          HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-02"), trackingId, HONGKONG_TO_NEW_YORK.voyageNumber(), HONGKONG.unLocode(), HandlingActivityType.LOAD, new OperatorCode("ABCDE")
          );
          session.save(event2);

          HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-05"), trackingId, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingActivityType.UNLOAD, new OperatorCode("ABCDE")
          );
          session.save(event3);
        } catch (CannotCreateHandlingEventException e) {
          throw new RuntimeException(e);
        }

        final HandlingEvent handlingEvent = handlingEventRepository.mostRecentHandling(abc123);
        abc123.handled(handlingEvent.activity());
        session.update(abc123);

        // Cargo JKL567

        RouteSpecification routeSpecification1 = new RouteSpecification(HANGZOU, STOCKHOLM, toDate("2009-03-18"));
        TrackingId trackingId1 = new TrackingId("JKL567");
        Cargo jkl567 = new Cargo(trackingId1, routeSpecification1);

        Itinerary itinerary1 = new Itinerary(asList(
          new Leg(HONGKONG_TO_NEW_YORK, HANGZOU, NEWYORK, toDate("2009-03-03"), toDate("2009-03-05")),
          new Leg(NEW_YORK_TO_DALLAS, NEWYORK, DALLAS, toDate("2009-03-06"), toDate("2009-03-08")),
          new Leg(DALLAS_TO_HELSINKI, DALLAS, STOCKHOLM, toDate("2009-03-09"), toDate("2009-03-11"))
        ));
        jkl567.assignToRoute(itinerary1);

        session.save(jkl567);

        try {
          HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-01"), trackingId1, null, HANGZOU.unLocode(), HandlingActivityType.RECEIVE, new OperatorCode("ABCDE")
          );
          session.save(event1);

          HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-03"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), HANGZOU.unLocode(), HandlingActivityType.LOAD, new OperatorCode("ABCDE")
          );
          session.save(event2);

          HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-05"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingActivityType.UNLOAD, new OperatorCode("ABCDE")
          );
          session.save(event3);

          HandlingEvent event4 = handlingEventFactory.createHandlingEvent(
            toDate("2009-03-06"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingActivityType.LOAD, new OperatorCode("ABCDE")
          );
          session.save(event4);

        } catch (CannotCreateHandlingEventException e) {
          throw new RuntimeException(e);
        }

        HandlingEvent handlingEvent1 = handlingEventRepository.mostRecentHandling(jkl567);
        jkl567.handled(handlingEvent1.activity());
        session.update(jkl567);
      }
    });
  }

  public void contextDestroyed(ServletContextEvent event) {
  }

  public static void loadSampleData(final JdbcTemplate jdbcTemplate, TransactionTemplate transactionTemplate) {
    transactionTemplate.execute(new TransactionCallbackWithoutResult() {
      protected void doInTransactionWithoutResult(TransactionStatus status) {
        loadLocationData(jdbcTemplate);
        loadCarrierMovementData(jdbcTemplate);
        loadCargoData(jdbcTemplate);
        loadItineraryData(jdbcTemplate);
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
