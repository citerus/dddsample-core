package se.citerus.dddsample.infrastructure.persistence.jdbi;

import com.google.common.collect.ImmutableList;
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
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.*;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={TestInfrastructurePersistenceConfig.class, InfrastructurePersistenceConfig.class})
@TestPropertySource(locations = {"/application.properties"})
public class CarrierMovementRepositoryTest {

    @Autowired
    private VoyageRepository voyageRepository;

    @BeforeClass
    public static void setupOnce() {
        Jdbi jdbi = Jdbi.create("jdbc:hsqldb:mem:dddsample_test", "sa", "");
        Flyway flyway = Flyway.configure().dataSource("jdbc:hsqldb:mem:dddsample_test", "sa", "").load();
        flyway.migrate();
        SampleDataGenerator.loadLocationData(jdbi);
        SampleDataGenerator.loadCarrierMovementData(jdbi);
    }

    @Test
    public void testFind() {
        Voyage voyage = voyageRepository.find(new VoyageNumber("0101"));
        assertThat(voyage).isNotNull();
        assertThat(voyage.voyageNumber().idString()).isEqualTo("0101");
    /* TODO adapt
    assertThat(carrierMovement.departureLocation()).isEqualTo(STOCKHOLM);
    assertThat(carrierMovement.arrivalLocation()).isEqualTo(HELSINKI);
    assertThat(carrierMovement.departureTime()).isEqualTo(DateTestUtil.toDate("2007-09-23", "02:00"));
    assertThat(carrierMovement.arrivalTime()).isEqualTo(DateTestUtil.toDate("2007-09-23", "03:00"));
    */
    }

    @Test
    public void testStore() {
        VoyageNumber voyageNumber = new VoyageNumber("0404");
        Location dl = new Location(new UnLocode("SESTO"), "Stockholm");
        Location al = new Location(new UnLocode("FIHEL"), "Helsinki");
        Date departureTime = new Date(System.currentTimeMillis() - 6000000);
        Date arrivalTime = new Date(System.currentTimeMillis());
        List<CarrierMovement> cms = ImmutableList.of(new CarrierMovement(dl, al, departureTime, arrivalTime));
        ((VoyageRepositoryJdbi)voyageRepository).store(new Voyage(voyageNumber, new Schedule(cms)));
    }
}
