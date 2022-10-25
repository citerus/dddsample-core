package se.citerus.dddsample.infrastructure.persistence.jdbi;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import se.citerus.dddsample.TestInfrastructurePersistenceConfig;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.TestInfrastructurePersistenceConfig.truncateAllTables;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.RECEIVE;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM004;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestInfrastructurePersistenceConfig.class, InfrastructurePersistenceConfig.class})
@TestPropertySource(locations = {"/application.properties"})
public class CargoRepositoryTest {

    @Autowired
    private CargoRepository cargoRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private HandlingEventRepository handlingEventRepository;

    private static Jdbi jdbi;

    @BeforeClass
    public static void setup() {
        jdbi = Jdbi.create("jdbc:hsqldb:mem:dddsample_test", "sa", "");
        Flyway flyway = Flyway.configure().dataSource("jdbc:hsqldb:mem:dddsample_test", "sa", "").load();
        flyway.migrate();
    }

    @Before
    public void setUp() throws Exception {
        truncateAllTables(jdbi);
        SampleDataGenerator.loadSampleData(jdbi);
    }

    @Test
    public void testFindByCargoId() {
        final TrackingId trackingId = new TrackingId("FGH");
        final Cargo cargo = cargoRepository.find(trackingId);
        // assertThat(cargo.origin()).isEqualTo(STOCKHOLM); // invalid assertion: origin is always retrieved from route spec, unless the field is updated through Hibernate magic (reflection).
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

    private static void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingEvent.Type expectedEventType,
                                            Location expectedLocation, int completionTimeMs, int registrationTimeMs,
                                            Voyage voyage) {
        assertThat(event.type()).isEqualTo(expectedEventType);
        assertThat(event.location()).isEqualTo(expectedLocation);

        Date expectedCompletionTime = SampleDataGenerator.offset(completionTimeMs);
        assertThat(event.completionTime()).isEqualTo(expectedCompletionTime);

        Date expectedRegistrationTime = SampleDataGenerator.offset(registrationTimeMs);
        assertThat(event.registrationTime()).isEqualTo(expectedRegistrationTime);

        assertThat(event.voyage()).isEqualTo(voyage.voyageNumber());
        assertThat(event.cargo()).isEqualTo(cargo.trackingId());
    }

    @Test
    public void testFindByCargoIdUnknownId() {
        assertThat(cargoRepository.find(new TrackingId("UNKNOWN"))).isNull();
    }

    private static void assertLeg(Leg firstLeg, String vn, Location expectedFrom, Location expectedTo) {
        assertThat(firstLeg.voyage()).isEqualTo(new VoyageNumber(vn));
        assertThat(firstLeg.loadLocation()).isEqualTo(expectedFrom);
        assertThat(firstLeg.unloadLocation()).isEqualTo(expectedTo);
    }

    @Test
    public void testSave() {
        TrackingId trackingId = new TrackingId("AAA");
        Location origin = locationRepository.find(STOCKHOLM.unLocode());
        Location destination = locationRepository.find(MELBOURNE.unLocode());

        Cargo cargo = new Cargo(trackingId, new RouteSpecification(origin, destination, new Date()));

        cargo.assignToRoute(new Itinerary(Collections.singletonList(
                new Leg(
                        new VoyageNumber("0101"),
                        locationRepository.find(STOCKHOLM.unLocode()),
                        locationRepository.find(MELBOURNE.unLocode()),
                        new Date(), new Date())
        )));

        cargoRepository.store(cargo);

        Map<String, Object> map = jdbi.withHandle(h ->
                h.createQuery("select * from Cargo where trackingId = :trackingId")
                        .bind("trackingId", trackingId.idString()).mapToMap().findOnly());

        assertThat(map.get("trackingid")).isEqualTo("AAA");

        Map<String, Object> routeSpecMap = jdbi.withHandle(h -> h.createQuery("SELECT * FROM RouteSpecification WHERE id = :cargoId")
                .bind("cargoId", map.get("id"))
                .mapToMap()
                .findOnly());

        int originId = getLocationDbId(origin);
        assertThat((int) routeSpecMap.get("origin")).isEqualTo(originId);

        int destinationId = getLocationDbId(destination);
        assertThat((int) routeSpecMap.get("destination")).isEqualTo(destinationId);

        Cargo loadedCargo = cargoRepository.find(trackingId);
        assertThat(loadedCargo.itinerary().legs()).hasSize(1);
    }

    @Test
    public void testReplaceItinerary() {
        TrackingId trackingId = new TrackingId("FGH");
        Cargo cargo = cargoRepository.find(trackingId);
        assertThat(countLegsForCargo(trackingId).intValue()).isEqualTo(3);

        Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
        Location legTo = locationRepository.find(new UnLocode("DEHAM"));
        Itinerary newItinerary = new Itinerary(Collections.singletonList(new Leg(CM004.voyageNumber(), legFrom, legTo, new Date(), new Date())));

        cargo.assignToRoute(newItinerary);

        cargoRepository.store(cargo);

        assertThat(countLegsForCargo(trackingId).intValue()).isEqualTo(1);
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
        assertThat(trackingId.idString()).doesNotContainPattern("[a-z]+");

        TrackingId trackingId2 = cargoRepository.nextTrackingId();
        assertThat(trackingId2).isNotNull();
        assertThat(trackingId.equals(trackingId2)).isFalse();
    }

    private Integer getLocationDbId(Location location) {
        return jdbi.withHandle(h -> h.createQuery("SELECT id FROM Location WHERE unLocode = :unloCode")
                .bind("unloCode", location.unLocode().idString())
                .mapTo(Integer.class)
                .findOnly());
    }

    private static Integer countLegsForCargo(TrackingId trackingId) {
        return jdbi.withHandle(h -> h.createQuery("select count(*) from Leg where cargo = (SELECT id FROM Cargo WHERE trackingId = :cargo)")
                .bind("cargo", trackingId.idString())
                .mapTo(Integer.class)
                .findOnly());
    }
}