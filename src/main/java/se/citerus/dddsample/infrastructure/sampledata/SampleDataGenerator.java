package se.citerus.dddsample.infrastructure.sampledata;

import org.springframework.lang.NonNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static se.citerus.dddsample.application.util.DateUtils.toDate;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.*;

/**
 * Provides sample data.
 */
public class SampleDataGenerator {

    private static final Timestamp base = getBaseTimeStamp();

    private final CargoRepository cargoRepository;
    private final VoyageRepository voyageRepository;
    private final LocationRepository locationRepository;
    private final HandlingEventRepository handlingEventRepository;
    private final PlatformTransactionManager transactionManager;

    public SampleDataGenerator(@NonNull CargoRepository cargoRepository,
                               @NonNull VoyageRepository voyageRepository,
                               @NonNull LocationRepository locationRepository,
                               @NonNull HandlingEventRepository handlingEventRepository,
                               @NonNull PlatformTransactionManager transactionManager) {
        // TODO can the requireNonNull calls be replaced by annotations?
        this.cargoRepository = requireNonNull(cargoRepository);
        this.voyageRepository = requireNonNull(voyageRepository);
        this.locationRepository = requireNonNull(locationRepository);
        this.handlingEventRepository = requireNonNull(handlingEventRepository);
        this.transactionManager = requireNonNull(transactionManager);
    }

    public void generate() {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);

        HandlingEventFactory handlingEventFactory = new HandlingEventFactory(
                cargoRepository,
                voyageRepository,
                locationRepository);
        loadHibernateData(tt, handlingEventFactory);
    }

    public void loadHibernateData(TransactionTemplate tt, final HandlingEventFactory handlingEventFactory) {
        System.out.println("*** Loading Hibernate data ***");
        tt.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (Location location : SampleLocations.getAll()) {
                    locationRepository.store(location);
                }

                voyageRepository.store(HONGKONG_TO_NEW_YORK);
                voyageRepository.store(NEW_YORK_TO_DALLAS);
                voyageRepository.store(DALLAS_TO_HELSINKI);
                voyageRepository.store(HELSINKI_TO_HONGKONG);
                voyageRepository.store(DALLAS_TO_HELSINKI_ALT);

                RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, toDate("2024-03-15"));
                TrackingId trackingId = new TrackingId("ABC123");
                Cargo abc123 = new Cargo(trackingId, routeSpecification);

                Itinerary itinerary = new Itinerary(List.of(
                        new Leg(HONGKONG_TO_NEW_YORK, HONGKONG, NEWYORK, toDate("2024-03-02"), toDate("2024-03-05")),
                        new Leg(NEW_YORK_TO_DALLAS, NEWYORK, DALLAS, toDate("2024-03-06"), toDate("2024-03-08")),
                        new Leg(DALLAS_TO_HELSINKI, DALLAS, HELSINKI, toDate("2024-03-09"), toDate("2024-03-12"))
                ));
                abc123.assignToRoute(itinerary);

                cargoRepository.store(abc123);

                try {
                    HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-01"), trackingId, null, HONGKONG.unLocode(), HandlingEvent.Type.RECEIVE
                    );
                    handlingEventRepository.store(event1);

                    HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-02"), trackingId, HONGKONG_TO_NEW_YORK.voyageNumber(), HONGKONG.unLocode(), HandlingEvent.Type.LOAD
                    );
                    handlingEventRepository.store(event2);

                    HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-05"), trackingId, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingEvent.Type.UNLOAD
                    );
                    handlingEventRepository.store(event3);
                } catch (CannotCreateHandlingEventException e) {
                    throw new RuntimeException(e);
                }

                HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);
                abc123.deriveDeliveryProgress(handlingHistory);

                cargoRepository.store(abc123);

                // Cargo JKL567

                RouteSpecification routeSpecification1 = new RouteSpecification(HANGZHOU, STOCKHOLM, toDate("2024-03-18"));
                TrackingId trackingId1 = new TrackingId("JKL567");
                Cargo jkl567 = new Cargo(trackingId1, routeSpecification1);

                Itinerary itinerary1 = new Itinerary(List.of(
                        new Leg(HONGKONG_TO_NEW_YORK, HANGZHOU, NEWYORK, toDate("2024-03-03"), toDate("2024-03-05")),
                        new Leg(NEW_YORK_TO_DALLAS, NEWYORK, DALLAS, toDate("2024-03-06"), toDate("2024-03-08")),
                        new Leg(DALLAS_TO_HELSINKI, DALLAS, STOCKHOLM, toDate("2024-03-09"), toDate("2024-03-11"))
                ));
                jkl567.assignToRoute(itinerary1);

                cargoRepository.store(jkl567);

                try {
                    HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-01"), trackingId1, null, HANGZHOU.unLocode(), HandlingEvent.Type.RECEIVE
                    );
                    handlingEventRepository.store(event1);

                    HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-03"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), HANGZHOU.unLocode(), HandlingEvent.Type.LOAD
                    );
                    handlingEventRepository.store(event2);

                    HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-05"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingEvent.Type.UNLOAD
                    );
                    handlingEventRepository.store(event3);

                    HandlingEvent event4 = handlingEventFactory.createHandlingEvent(
                            Instant.now(), toDate("2024-03-06"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingEvent.Type.LOAD
                    );
                    handlingEventRepository.store(event4);

                } catch (CannotCreateHandlingEventException e) {
                    throw new RuntimeException(e);
                }

                HandlingHistory handlingHistory1 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId1);
                jkl567.deriveDeliveryProgress(handlingHistory1);

                cargoRepository.store(jkl567);
            }
        });
    }

    private static Timestamp ts(int hours) {
        return new Timestamp(base.getTime() + 1000L * 60 * 60 * hours);
    }

    public static Instant offset(int hours) {
        return Instant.ofEpochMilli(ts(hours).getTime());
    }

    private static Timestamp getBaseTimeStamp() {
        try {
            LocalDate date = LocalDate.parse("2008-01-01");
            return new Timestamp(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000 - 1000L * 60 * 60 * 24 * 100);
        } catch (DateTimeParseException e) {
            throw new RuntimeException(e);
        }
    }
}
