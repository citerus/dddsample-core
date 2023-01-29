package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = TestRepositoryConfig.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class HandlingEventRepositoryTest {

    @Autowired
    HandlingEventRepository handlingEventRepository;

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    public void testSave() {
        Location location = locationRepository.find(new UnLocode("SESTO"));

        Cargo cargo = cargoRepository.find(new TrackingId("ABC123"));
        Date completionTime = new Date(10);
        Date registrationTime = new Date(20);
        HandlingEvent event = new HandlingEvent(cargo, completionTime, registrationTime, HandlingEvent.Type.CLAIM, location);

        handlingEventRepository.store(event);

        flush();

        HandlingEvent result = entityManager.createQuery(String.format("select he from HandlingEvent he where he.id = %d", event.id), HandlingEvent.class).getSingleResult();

        assertThat(result.cargo.id).isEqualTo(cargo.id);
        Date completionDate = result.completionTime;
        assertThat(completionDate).isEqualTo(new Date(10));
        Date registrationDate = result.registrationTime;
        assertThat(registrationDate).isEqualTo(new Date(20));
        assertThat(result.type).isEqualTo(HandlingEvent.Type.CLAIM);
        // TODO: the rest of the columns
    }

    private void flush() {
        entityManager.flush();
    }

    @Test
    public void testFindEventsForCargo() {
        TrackingId trackingId = new TrackingId("ABC123");
        List<HandlingEvent> handlingEvents = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertThat(handlingEvents).hasSize(3);
    }

}