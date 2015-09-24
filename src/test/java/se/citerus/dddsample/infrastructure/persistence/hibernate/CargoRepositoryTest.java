package se.citerus.dddsample.infrastructure.persistence.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.RECEIVE;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM004;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = {"/context-infrastructure-persistence.xml", "/context-domain.xml"})
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class CargoRepositoryTest {

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    VoyageRepository voyageRepository;

    @Autowired
    HandlingEventRepository handlingEventRepository;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private HibernateTransactionManager transactionManager;

    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        SampleDataGenerator.loadSampleData(jdbcTemplate, new TransactionTemplate(transactionManager));
    }

    @Test
    public void testFindByCargoId() {
        final TrackingId trackingId = new TrackingId("FGH");
        final Cargo cargo = cargoRepository.find(trackingId);
        assertEquals(STOCKHOLM, cargo.origin());
        assertEquals(HONGKONG, cargo.routeSpecification().origin());
        assertEquals(HELSINKI, cargo.routeSpecification().destination());

        assertNotNull(cargo.delivery());

        final List<HandlingEvent> events = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertEquals(2, events.size());

        HandlingEvent firstEvent = events.get(0);
        assertHandlingEvent(cargo, firstEvent, RECEIVE, HONGKONG, 100, 160, Voyage.NONE);

        HandlingEvent secondEvent = events.get(1);

        Voyage hongkongMelbourneTokyoAndBack = new Voyage.Builder(
                new VoyageNumber("0303"), HONGKONG).
                addMovement(MELBOURNE, new Date(), new Date()).
                addMovement(TOKYO, new Date(), new Date()).
                addMovement(HONGKONG, new Date(), new Date()).
                build();

        assertHandlingEvent(cargo, secondEvent, LOAD, HONGKONG, 150, 110, hongkongMelbourneTokyoAndBack);

        List<Leg> legs = cargo.itinerary().legs();
        assertEquals(3, legs.size());

        Leg firstLeg = legs.get(0);
        assertLeg(firstLeg, "0101", HONGKONG, MELBOURNE);

        Leg secondLeg = legs.get(1);
        assertLeg(secondLeg, "0101", MELBOURNE, STOCKHOLM);

        Leg thirdLeg = legs.get(2);
        assertLeg(thirdLeg, "0101", STOCKHOLM, HELSINKI);
    }

    private void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingEvent.Type expectedEventType, Location expectedLocation, int completionTimeMs, int registrationTimeMs, Voyage voyage) {
        assertEquals(expectedEventType, event.type());
        assertEquals(expectedLocation, event.location());

        Date expectedCompletionTime = SampleDataGenerator.offset(completionTimeMs);
        assertEquals(expectedCompletionTime, event.completionTime());

        Date expectedRegistrationTime = SampleDataGenerator.offset(registrationTimeMs);
        assertEquals(expectedRegistrationTime, event.registrationTime());

        assertEquals(voyage, event.voyage());
        assertEquals(cargo, event.cargo());
    }

    @Test
    public void testFindByCargoIdUnknownId() {
        assertNull(cargoRepository.find(new TrackingId("UNKNOWN")));
    }

    private void assertLeg(Leg firstLeg, String vn, Location expectedFrom, Location expectedTo) {
        assertEquals(new VoyageNumber(vn), firstLeg.voyage().voyageNumber());
        assertEquals(expectedFrom, firstLeg.loadLocation());
        assertEquals(expectedTo, firstLeg.unloadLocation());
    }

    @Test
    public void testSave() {
        TrackingId trackingId = new TrackingId("AAA");
        Location origin = locationRepository.find(STOCKHOLM.unLocode());
        Location destination = locationRepository.find(MELBOURNE.unLocode());

        Cargo cargo = new Cargo(trackingId, new RouteSpecification(origin, destination, new Date()));
        cargoRepository.store(cargo);

        cargo.assignToRoute(new Itinerary(Collections.singletonList(
                new Leg(
                        voyageRepository.find(new VoyageNumber("0101")),
                        locationRepository.find(STOCKHOLM.unLocode()),
                        locationRepository.find(MELBOURNE.unLocode()),
                        new Date(), new Date())
        )));

        flush();

        Map<String, Object> map = jdbcTemplate.queryForMap(
                "select * from Cargo where tracking_id = ?", trackingId.idString());

        assertEquals("AAA", map.get("TRACKING_ID"));

        Long originId = getLongId(origin);
        assertEquals(originId, map.get("SPEC_ORIGIN_ID"));

        Long destinationId = getLongId(destination);
        assertEquals(destinationId, map.get("SPEC_DESTINATION_ID"));

        sessionFactory.getCurrentSession().clear();

        final Cargo loadedCargo = cargoRepository.find(trackingId);
        assertEquals(1, loadedCargo.itinerary().legs().size());
    }

    @Test
    public void testReplaceItinerary() {
        Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
        Long cargoId = getLongId(cargo);
        assertEquals(3, jdbcTemplate.queryForObject("select count(*) from Leg where cargo_id = ?", new Object[]{cargoId}, Integer.class).intValue());

        Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
        Location legTo = locationRepository.find(new UnLocode("DEHAM"));
        Itinerary newItinerary = new Itinerary(Collections.singletonList(new Leg(CM004, legFrom, legTo, new Date(), new Date())));

        cargo.assignToRoute(newItinerary);

        cargoRepository.store(cargo);
        flush();

        assertEquals(1, jdbcTemplate.queryForObject("select count(*) from Leg where cargo_id = ?", new Object[]{cargoId}, Integer.class).intValue());
    }

    @Test
    public void testFindAll() {
        List<Cargo> all = cargoRepository.findAll();
        assertNotNull(all);
        assertEquals(6, all.size());
    }

    @Test
    public void testNextTrackingId() {
        TrackingId trackingId = cargoRepository.nextTrackingId();
        assertNotNull(trackingId);

        TrackingId trackingId2 = cargoRepository.nextTrackingId();
        assertNotNull(trackingId2);
        assertFalse(trackingId.equals(trackingId2));
    }


    private void flush() {
        sessionFactory.getCurrentSession().flush();
    }

    private Long getLongId(Object o) {
        final Session session = sessionFactory.getCurrentSession();
        if (session.contains(o)) {
            return (Long) session.getIdentifier(o);
        } else {
            try {
                Field id = o.getClass().getDeclaredField("id");
                id.setAccessible(true);
                return (Long) id.get(o);
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

}