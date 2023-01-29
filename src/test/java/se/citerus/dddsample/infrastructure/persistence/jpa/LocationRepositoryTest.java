package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = TestRepositoryConfig.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class LocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepository;

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
        List<Location> allLocations = locationRepository.getAll();

        assertThat(allLocations).isNotNull().hasSize(13);
    }

}
