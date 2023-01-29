package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = TestRepositoryConfig.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class CarrierMovementRepositoryTest {

    @Autowired
    VoyageRepository voyageRepository;

    @Test
    public void testFind() {
        Voyage voyage = voyageRepository.find(new VoyageNumber("0100S"));
        assertThat(voyage).isNotNull();
        assertThat(voyage.voyageNumber().idString()).isEqualTo("0100S");
    /* TODO adapt
    assertThat(carrierMovement.departureLocation()).isEqualTo(STOCKHOLM);
    assertThat(carrierMovement.arrivalLocation()).isEqualTo(HELSINKI);
    assertThat(carrierMovement.departureTime()).isEqualTo(DateTestUtil.toDate("2007-09-23", "02:00"));
    assertThat(carrierMovement.arrivalTime()).isEqualTo(DateTestUtil.toDate("2007-09-23", "03:00"));
    */
    }

}
