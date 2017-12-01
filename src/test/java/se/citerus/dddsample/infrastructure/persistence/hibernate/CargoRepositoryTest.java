package se.citerus.dddsample.infrastructure.persistence.hibernate;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.RECEIVE;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HONGKONG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.MELBOURNE;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import static se.citerus.dddsample.domain.model.location.SampleLocations.TOKYO;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM004;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

@RunWith(SpringRunner.class)
@ContextConfiguration(value = {"/context-infrastructure-persistence.xml"})
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
        assertThat(cargo.origin()).isEqualTo(STOCKHOLM);
        assertThat(cargo.routeSpecification().origin()).isEqualTo(HONGKONG);
        assertThat(cargo.routeSpecification().destination()).isEqualTo(HELSINKI);

        assertThat(cargo.delivery()).isNotNull();

        final List<HandlingEvent> events = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertThat(events).hasSize(2);

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
        assertThat(legs).hasSize(3);

        Leg firstLeg = legs.get(0);
        assertLeg(firstLeg, "0101", HONGKONG, MELBOURNE);

        Leg secondLeg = legs.get(1);
        assertLeg(secondLeg, "0101", MELBOURNE, STOCKHOLM);

        Leg thirdLeg = legs.get(2);
        assertLeg(thirdLeg, "0101", STOCKHOLM, HELSINKI);
    }

    private void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingEvent.Type expectedEventType, Location expectedLocation, int completionTimeMs, int registrationTimeMs, Voyage voyage) {
        assertThat(event.type()).isEqualTo(expectedEventType);
        assertThat(event.location()).isEqualTo(expectedLocation);

        Date expectedCompletionTime = SampleDataGenerator.offset(completionTimeMs);
        assertThat(event.completionTime()).isEqualTo(expectedCompletionTime);

        Date expectedRegistrationTime = SampleDataGenerator.offset(registrationTimeMs);
        assertThat(event.registrationTime()).isEqualTo(expectedRegistrationTime);

        assertThat(event.voyage()).isEqualTo(voyage);
        assertThat(event.cargo()).isEqualTo(cargo);
    }

    @Test
    public void testFindByCargoIdUnknownId() {
        assertThat(cargoRepository.find(new TrackingId("UNKNOWN"))).isNull();
    }

    private void assertLeg(Leg firstLeg, String vn, Location expectedFrom, Location expectedTo) {
        assertThat(firstLeg.voyage().voyageNumber()).isEqualTo(new VoyageNumber(vn));
        assertThat(firstLeg.loadLocation()).isEqualTo(expectedFrom);
        assertThat(firstLeg.unloadLocation()).isEqualTo(expectedTo);
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

        assertThat(map.get("TRACKING_ID")).isEqualTo("AAA");

        Long originId = getLongId(origin);
        assertThat(map.get("SPEC_ORIGIN_ID")).isEqualTo(originId);

        Long destinationId = getLongId(destination);
        assertThat(map.get("SPEC_DESTINATION_ID")).isEqualTo(destinationId);

        sessionFactory.getCurrentSession().clear();

        final Cargo loadedCargo = cargoRepository.find(trackingId);
        assertThat(loadedCargo.itinerary().legs()).hasSize(1);
    }

    @Test
    public void testReplaceItinerary() {
        Cargo cargo = cargoRepository.find(new TrackingId("FGH"));
        Long cargoId = getLongId(cargo);
        assertThat(jdbcTemplate.queryForObject("select count(*) from Leg where cargo_id = ?", new Object[]{cargoId}, Integer.class).intValue()).isEqualTo(3);

        Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
        Location legTo = locationRepository.find(new UnLocode("DEHAM"));
        Itinerary newItinerary = new Itinerary(Collections.singletonList(new Leg(CM004, legFrom, legTo, new Date(), new Date())));

        cargo.assignToRoute(newItinerary);

        cargoRepository.store(cargo);
        flush();

        assertThat(jdbcTemplate.queryForObject("select count(*) from Leg where cargo_id = ?", new Object[]{cargoId}, Integer.class).intValue()).isEqualTo(1);
    }

    @Test
    public void testFindAll() {
        List<Cargo> all = cargoRepository.findAll();
        assertThat(all).isNotNull();
        assertThat(all).hasSize(6);
    }

    @Test
    public void testNextTrackingId() {
        TrackingId trackingId = cargoRepository.nextTrackingId();
        assertThat(trackingId).isNotNull();

        TrackingId trackingId2 = cargoRepository.nextTrackingId();
        assertThat(trackingId2).isNotNull();
        assertThat(trackingId.equals(trackingId2)).isFalse();
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