package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import javax.persistence.EntityManager;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.application.util.DateUtils.toDate;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.LOAD;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.RECEIVE;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.HELSINKI_TO_HONGKONG;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.NEW_YORK_TO_DALLAS;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@ContextConfiguration(classes = TestRepositoryConfig.class)
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
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
    EntityManager entityManager;

    @Test
    public void testFindByCargoId() {
        final TrackingId trackingId = new TrackingId("ABC123");
        final Cargo cargo = cargoRepository.find(trackingId);
        assertThat(cargo).isNotNull();
        assertThat(cargo.origin()).isEqualTo(HONGKONG);
        assertThat(cargo.routeSpecification().origin()).isEqualTo(HONGKONG);
        assertThat(cargo.routeSpecification().destination()).isEqualTo(HELSINKI);

        assertThat(cargo.delivery()).isNotNull();

        final List<HandlingEvent> events = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId).distinctEventsByCompletionTime();
        assertThat(events).hasSize(3);

        HandlingEvent firstEvent = events.get(0);
        assertHandlingEvent(cargo, firstEvent, RECEIVE, HONGKONG, toDate("2009-03-01"), new Date(), Voyage.NONE.voyageNumber());

        HandlingEvent secondEvent = events.get(1);

        assertHandlingEvent(cargo, secondEvent, LOAD, HONGKONG, toDate("2009-03-02"), new Date(), new VoyageNumber("0100S"));

        List<Leg> legs = cargo.itinerary().legs();
        assertThat(legs).hasSize(3)
                .extracting("voyage.voyageNumber", "loadLocation", "unloadLocation")
                .containsExactly(
                    Tuple.tuple(null, HONGKONG, NEWYORK),
                    Tuple.tuple("0200T", NEWYORK, DALLAS),
                    Tuple.tuple("0300A", DALLAS, HELSINKI));
    }

    private void assertHandlingEvent(Cargo cargo, HandlingEvent event, HandlingEvent.Type expectedEventType,
                                     Location expectedLocation, Date expectedCompletionTime, Date expectedRegistrationTime, VoyageNumber voyage) {
        assertThat(event.type()).isEqualTo(expectedEventType);
        assertThat(event.location()).isEqualTo(expectedLocation);

        assertThat(event.completionTime()).isEqualTo(expectedCompletionTime);

        assertThat(event.registrationTime()).isEqualToIgnoringSeconds(expectedRegistrationTime);

        assertThat(event.voyage().voyageNumber()).isEqualTo(voyage);
        assertThat(event.cargo()).isEqualTo(cargo);
    }

    @Test
    public void testFindByCargoIdUnknownId() {
        assertThat(cargoRepository.find(new TrackingId("UNKNOWN"))).isNull();
    }

    @Test
    public void testSave() {
        TrackingId trackingId = new TrackingId("AAA");
        Location origin = locationRepository.find(STOCKHOLM.unLocode());
        Location destination = locationRepository.find(MELBOURNE.unLocode());

        Cargo cargo = new Cargo(trackingId, new RouteSpecification(origin, destination, new Date()));
        cargoRepository.store(cargo);

        Voyage voyage = voyageRepository.find(NEW_YORK_TO_DALLAS.voyageNumber());
        assertThat(voyage).isNotNull();
        cargo.assignToRoute(new Itinerary(List.of(
                new Leg(
                        voyage,
                        locationRepository.find(STOCKHOLM.unLocode()),
                        locationRepository.find(MELBOURNE.unLocode()),
                        new Date(), new Date())
        )));

        flush();

        Cargo result = entityManager.createQuery(
                String.format("from Cargo c where c.trackingId = '%s'", trackingId.idString()), Cargo.class).getSingleResult();
        assertThat(result.trackingId().idString()).isEqualTo("AAA");
        assertThat(result.routeSpecification.origin.id).isEqualTo(origin.id);
        assertThat(result.routeSpecification.destination.id).isEqualTo(destination.id);

        entityManager.clear();

        final Cargo loadedCargo = cargoRepository.find(trackingId);
        assertThat(loadedCargo.itinerary().legs()).hasSize(1);
    }

    @Test
    public void testReplaceItinerary() {
        Cargo cargo = cargoRepository.find(new TrackingId("JKL567"));
        assertThat(cargo).isNotNull();
        long cargoId = cargo.id;
        assertThat(countLegsForCargo(cargoId)).isEqualTo(3);

        Location legFrom = locationRepository.find(new UnLocode("FIHEL"));
        Location legTo = locationRepository.find(new UnLocode("CNHKG"));
        Voyage voyage = voyageRepository.find(HELSINKI_TO_HONGKONG.voyageNumber());
        Itinerary newItinerary = new Itinerary(List.of(new Leg(voyage, legFrom, legTo, new Date(), new Date())));

        cargo.assignToRoute(newItinerary);

        cargoRepository.store(cargo);
        flush();

        assertThat(countLegsForCargo(cargoId)).isEqualTo(1);
    }

    @Test
    public void testFindAll() {
        List<Cargo> all = cargoRepository.getAll();
        assertThat(all).isNotNull();
        assertThat(all).hasSize(2);
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
        entityManager.flush();
    }

    private int countLegsForCargo(long cargoId) {
        return ((BigInteger) entityManager.createNativeQuery(String.format("select count(*) from Leg l where l.cargo_id = %d", cargoId)).getSingleResult()).intValue();
    }
}