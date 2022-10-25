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
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestInfrastructurePersistenceConfig.class, InfrastructurePersistenceConfig.class})
@TestPropertySource(locations = {"/application.properties"})
public class LocationRepositoryTest {

    @Autowired
    private LocationRepository locationRepository;

    @BeforeClass
    public static void setupOnce() {
        Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:dddsample_test", "sa", "");
        Flyway flyway = Flyway.configure().dataSource("jdbc:hsqldb:mem:dddsample_test", "sa", "").load();
        flyway.migrate();
        SampleDataGenerator.loadLocationData(jdbi);
    }

    @Test
    public void testStore() {
        ((LocationRepositoryJdbi)locationRepository).store(new Location(new UnLocode("NOOSL"), "Oslo"));
    }

    @Test
    public void testFind() {
        final UnLocode melbourne = new UnLocode("AUMEL");
        Location location = locationRepository.find(melbourne);
        assertThat(location).isNotNull();
        assertThat(location.unLocode()).isEqualTo(melbourne);

        assertThat(locationRepository.find(new UnLocode("NOLOC"))).isNull();
    }

    @Test
    public void testFindAll() {
        List<Location> allLocations = locationRepository.findAll();

        assertThat(allLocations).isNotNull();
        assertThat(allLocations).hasSize(7);
    }

}
