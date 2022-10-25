package se.citerus.dddsample.infrastructure.persistence.jdbi;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import se.citerus.dddsample.TestInfrastructurePersistenceConfig;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestInfrastructurePersistenceConfig.class, InfrastructurePersistenceConfig.class})
@TestPropertySource(locations = {"/application.properties"})
public class HandlingEventRepositoryTest {

    @Autowired
    private HandlingEventRepository handlingEventRepository;

    @Autowired
    private LocationRepository locationRepository;

    @BeforeClass
    public static void setupOnce() {
        Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:dddsample_test", "sa", "");
        Flyway flyway = Flyway.configure().dataSource("jdbc:hsqldb:mem:dddsample_test", "sa", "").load();
        flyway.migrate();
        SampleDataGenerator.loadLocationData(jdbi);
        jdbi.useHandle(h -> h.createUpdate("INSERT INTO Cargo(trackingId) VALUES('XYZ')").execute());
        SampleDataGenerator.loadCarrierMovementData(jdbi);
        SampleDataGenerator.loadHandlingEventData(jdbi);
    }

    @Test
    public void testSave() {
        Location location = locationRepository.find(new UnLocode("SESTO"));

        TrackingId xyz = new TrackingId("XYZ");
        Date completionTime = new Date(10);
        Date registrationTime = new Date(20);
        HandlingEvent event = new HandlingEvent(xyz, completionTime, registrationTime, HandlingEvent.Type.CLAIM, location);

        handlingEventRepository.store(event);

        HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(xyz);

        HandlingEvent result = handlingHistory.distinctEventsByCompletionTime().get(0);
        assertThat(result.cargo()).isEqualTo(xyz);
        assertThat(result.completionTime()).isEqualTo(new Date(10));
        assertThat(result.registrationTime()).isEqualTo(new Date(20));
        assertThat(result.type()).isEqualTo(HandlingEvent.Type.CLAIM);
        // TODO: the rest of the columns
    }

    @Test
    public void testFindEventsForCargo() {
        TrackingId trackingId = new TrackingId("XYZ");
        List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId)
                .distinctEventsByCompletionTime();
        assertThat(handlingEvents).hasSize(12);
    }
}