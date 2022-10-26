package se.citerus.dddsample.application.util;

import com.google.common.collect.ImmutableMap;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.argument.NullArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.SampleLocations;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.jdbi.CargoRepositoryJdbi;
import se.citerus.dddsample.infrastructure.persistence.jdbi.LocationRepositoryJdbi;
import se.citerus.dddsample.infrastructure.persistence.jdbi.VoyageRepositoryJdbi;

import java.lang.invoke.MethodHandles;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.*;

/**
 * Provides sample data.
 */
public class SampleDataGenerator {
    private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private static final Timestamp base;

    static {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse("2008-01-01");
            base = new Timestamp(date.getTime() - 1000L * 60 * 60 * 24 * 100);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private final CargoRepository cargoRepository;
    private final VoyageRepository voyageRepository;
    private final LocationRepository locationRepository;
    private final HandlingEventRepository handlingEventRepository;

    public SampleDataGenerator(CargoRepository cargoRepository,
                               VoyageRepository voyageRepository,
                               LocationRepository locationRepository,
                               HandlingEventRepository handlingEventRepository) {
        this.cargoRepository = requireNonNull(cargoRepository);
        this.voyageRepository = requireNonNull(voyageRepository);
        this.locationRepository = requireNonNull(locationRepository);
        this.handlingEventRepository = requireNonNull(handlingEventRepository);
    }

    public void generate() {
        HandlingEventFactory handlingEventFactory = new HandlingEventFactory(
                cargoRepository, voyageRepository,
                locationRepository);
        loadSampleData(handlingEventFactory, handlingEventRepository);
    }

    public void loadSampleData(final HandlingEventFactory handlingEventFactory, final HandlingEventRepository handlingEventRepository) {
        log.info("*** Loading sample data ***");
        for (Location location : SampleLocations.getAll()) {
            ((LocationRepositoryJdbi) locationRepository).store(location);
        }

        VoyageRepositoryJdbi voyageRepositoryJdbi = (VoyageRepositoryJdbi) voyageRepository;
        voyageRepositoryJdbi.store(HONGKONG_TO_NEW_YORK);
        voyageRepositoryJdbi.store(NEW_YORK_TO_DALLAS);
        voyageRepositoryJdbi.store(DALLAS_TO_HELSINKI);
        voyageRepositoryJdbi.store(HELSINKI_TO_HONGKONG);
        voyageRepositoryJdbi.store(DALLAS_TO_HELSINKI_ALT);

        RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, HELSINKI, toDate("2009-03-15"));
        TrackingId trackingId = new TrackingId("ABC123");
        Cargo abc123 = new Cargo(trackingId, routeSpecification);

        Itinerary itinerary = new Itinerary(Arrays.asList(
                new Leg(HONGKONG_TO_NEW_YORK.voyageNumber(), HONGKONG, NEWYORK, toDate("2009-03-02"), toDate("2009-03-05")),
                new Leg(NEW_YORK_TO_DALLAS.voyageNumber(), NEWYORK, DALLAS, toDate("2009-03-06"), toDate("2009-03-08")),
                new Leg(DALLAS_TO_HELSINKI.voyageNumber(), DALLAS, HELSINKI, toDate("2009-03-09"), toDate("2009-03-12"))
        ));
        abc123.assignToRoute(itinerary);

        cargoRepository.store(abc123);

        try {
            HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-01"), trackingId, null, HONGKONG.unLocode(), HandlingEvent.Type.RECEIVE
            );
            handlingEventRepository.store(event1);

            HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-02"), trackingId, HONGKONG_TO_NEW_YORK.voyageNumber(), HONGKONG.unLocode(), HandlingEvent.Type.LOAD
            );
            handlingEventRepository.store(event2);

            HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-05"), trackingId, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingEvent.Type.UNLOAD
            );
            handlingEventRepository.store(event3);
        } catch (CannotCreateHandlingEventException e) {
            throw new RuntimeException(e);
        }

        HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId);
        abc123.deriveDeliveryProgress(handlingHistory);

        ((CargoRepositoryJdbi) cargoRepository).update(abc123);

        // Cargo JKL567

        RouteSpecification routeSpecification1 = new RouteSpecification(HANGZHOU, STOCKHOLM, toDate("2009-03-18"));
        TrackingId trackingId1 = new TrackingId("JKL567");
        Cargo jkl567 = new Cargo(trackingId1, routeSpecification1);

        Itinerary itinerary1 = new Itinerary(Arrays.asList(
                new Leg(HONGKONG_TO_NEW_YORK.voyageNumber(), HANGZHOU, NEWYORK, toDate("2009-03-03"), toDate("2009-03-05")),
                new Leg(NEW_YORK_TO_DALLAS.voyageNumber(), NEWYORK, DALLAS, toDate("2009-03-06"), toDate("2009-03-08")),
                new Leg(DALLAS_TO_HELSINKI.voyageNumber(), DALLAS, STOCKHOLM, toDate("2009-03-09"), toDate("2009-03-11"))
        ));
        jkl567.assignToRoute(itinerary1);

        cargoRepository.store(jkl567);

        try {
            HandlingEvent event1 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-01"), trackingId1, null, HANGZHOU.unLocode(), HandlingEvent.Type.RECEIVE
            );
            handlingEventRepository.store(event1);

            HandlingEvent event2 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-03"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), HANGZHOU.unLocode(), HandlingEvent.Type.LOAD
            );
            handlingEventRepository.store(event2);

            HandlingEvent event3 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-05"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingEvent.Type.UNLOAD
            );
            handlingEventRepository.store(event3);

            HandlingEvent event4 = handlingEventFactory.createHandlingEvent(
                    new Date(), toDate("2009-03-06"), trackingId1, HONGKONG_TO_NEW_YORK.voyageNumber(), NEWYORK.unLocode(), HandlingEvent.Type.LOAD
            );
            handlingEventRepository.store(event4);

        } catch (CannotCreateHandlingEventException e) {
            throw new RuntimeException(e);
        }

        HandlingHistory handlingHistory1 = handlingEventRepository.lookupHandlingHistoryOfCargo(trackingId1);
        jkl567.deriveDeliveryProgress(handlingHistory1);

        ((CargoRepositoryJdbi) cargoRepository).update(jkl567);
    }

    public static void loadSampleData(Jdbi jdbi) {
        loadLocationData(jdbi);
        loadCarrierMovementData(jdbi);
        loadCargoData(jdbi);
        loadItineraryData(jdbi);
        loadHandlingEventData(jdbi);
    }

    public static void loadHandlingEventData(Jdbi jdbi) {
        String handlingEventSql =
                "insert into HandlingEvent (completionTime, registrationTime, type, location, voyage, cargo) " +
                        "values (:completionTime, :registrationTime, :type, " +
                        "(SELECT id FROM Location WHERE unLocode = :locationId), " +
                        "(SELECT id FROM Voyage WHERE voyageNumber = :voyageId), " +
                        "(SELECT id FROM Cargo WHERE trackingId = :cargoId))";
        String handlingEventWithoutVoyageSql =
                "insert into HandlingEvent (completionTime, registrationTime, type, location, cargo) " +
                        "values (:completionTime, :registrationTime, :type, " +
                        "(SELECT id FROM Location WHERE unLocode = :locationId), " +
                        "(SELECT id FROM Cargo WHERE trackingId = :cargoId))";

        String[] keys = {
                "completionTime", "registrationTime", "type", "locationId", "voyageId", "cargoId"
        };
        Object[][] handlingEventArgs = {
                //XYZ (SESTO-FIHEL-DEHAM-CNHKG-JPTOK-AUMEL)
                {ts(0), ts((0)), "RECEIVE", "SESTO", null, "XYZ"},
                {ts((4)), ts((5)), "LOAD", "SESTO", "0101", "XYZ"},
                {ts((14)), ts((14)), "UNLOAD", "FIHEL", "0101", "XYZ"},
                {ts((15)), ts((15)), "LOAD", "FIHEL", "0101", "XYZ"},
                {ts((30)), ts((30)), "UNLOAD", "DEHAM", "0101", "XYZ"},
                {ts((33)), ts((33)), "LOAD", "DEHAM", "0101", "XYZ"},
                {ts((34)), ts((34)), "UNLOAD", "CNHKG", "0101", "XYZ"},
                {ts((60)), ts((60)), "LOAD", "CNHKG", "0101", "XYZ"},
                {ts((70)), ts((71)), "UNLOAD", "JPTOK", "0101", "XYZ"},
                {ts((75)), ts((75)), "LOAD", "JPTOK", "0101", "XYZ"},
                {ts((88)), ts((88)), "UNLOAD", "AUMEL", "0101", "XYZ"},
                {ts((100)), ts((102)), "CLAIM", "AUMEL", null, "XYZ"},

                //ZYX (AUMEL - USCHI - DEHAM -)
                {ts((200)), ts((201)), "RECEIVE", "AUMEL", null, "ZYX"},
                {ts((202)), ts((202)), "LOAD", "AUMEL", "0202", "ZYX"},
                {ts((208)), ts((208)), "UNLOAD", "USCHI", "0202", "ZYX"},
                {ts((212)), ts((212)), "LOAD", "USCHI", "0202", "ZYX"},
                {ts((230)), ts((230)), "UNLOAD", "DEHAM", "0202", "ZYX"},
                {ts((235)), ts((235)), "LOAD", "DEHAM", "0202", "ZYX"},

                //ABC
                {ts((20)), ts((21)), "CLAIM", "AUMEL", null, "ABC"},

                //CBA
                {ts((0)), ts((1)), "RECEIVE", "AUMEL", null, "CBA"},
                {ts((10)), ts((11)), "LOAD", "AUMEL", "0202", "CBA"},
                {ts((20)), ts((21)), "UNLOAD", "USCHI", "0202", "CBA"},

                //FGH
                {ts(100), ts(160), "RECEIVE", "CNHKG", null, "FGH"},
                {ts(150), ts(110), "LOAD", "CNHKG", "0303", "FGH"},

                // JKL
                {ts(200), ts(220), "RECEIVE", "DEHAM", null, "JKL"},
                {ts(300), ts(330), "LOAD", "DEHAM", "0303", "JKL"},
                {ts(400), ts(440), "UNLOAD", "FIHEL", "0303", "JKL"}  // Unexpected event
        };
        jdbi.useHandle(h -> {
            for (Object[] handlingEvent : handlingEventArgs) {
                Map<String, Object> map = joinToMap(keys, handlingEvent);
                if (map.get("voyageId") instanceof NullArgument) {
                    map.remove("voyageId");
                    h.createUpdate(handlingEventWithoutVoyageSql)
                            .bindMap(map)
                            .execute();
                } else {
                    h.createUpdate(handlingEventSql)
                            .bindMap(map)
                            .execute();
                }
            }
        });
    }

    private static void loadItineraryData(Jdbi jdbi) {
        String legSql =
                "insert into Leg (cargo, voyage, loadLocation, unloadLocation, loadTime, unloadTime) " +
                        "values (" +
                        "(SELECT id FROM Cargo WHERE trackingId = :cargoId), " +
                        "(SELECT id FROM Voyage WHERE voyageNumber = :voyageId), " +
                        "(SELECT id FROM Location WHERE unLocode = :loadLocationId), " +
                        "(SELECT id FROM Location WHERE unLocode = :unloadLocationId), " +
                        ":loadTime, " +
                        ":unloadTime)";

        String[] keys = {
                "cargoId", "voyageId", "loadLocationId", "unloadLocationId", "loadTime", "unloadTime"
        };
        Object[][] legArgs = {
                // Cargo 5: Hongkong - Melbourne - Stockholm - Helsinki
                {"FGH", "0101", "CNHKG", "AUMEL", ts(1), ts(2)},
                {"FGH", "0101", "AUMEL", "SESTO", ts(3), ts(4)},
                {"FGH", "0101", "SESTO", "FIHEL", ts(4), ts(5)},
                // Cargo 6: Hamburg - Stockholm - Chicago - Tokyo
                {"JKL", "0202", "DEHAM", "SESTO", ts(1), ts(2)},
                {"JKL", "0202", "SESTO", "USCHI", ts(3), ts(4)},
                {"JKL", "0202", "USCHI", "JPTOK", ts(5), ts(6)}
        };
        jdbi.useHandle(h -> {
            for (Object[] leg : legArgs) {
                h.createUpdate(legSql)
                        .bindMap(joinToMap(keys, leg))
                        .execute();
            }
        });
    }

    private static void loadCargoData(Jdbi jdbi) {
        Object[][] routeSpecArgs = {
                {"SESTO", "AUMEL", ts(10)},
                {"SESTO", "FIHEL", ts(20)},
                {"AUMEL", "SESTO", ts(30)},
                {"FIHEL", "SESTO", ts(40)},
                {"CNHKG", "FIHEL", ts(50)},
                {"DEHAM", "JPTOK", ts(60)},
        };
        List<Integer> routeSpecIds = new ArrayList<>();
        jdbi.useHandle(h -> {
            for (Object[] routeSpec: routeSpecArgs) {
                h.createUpdate("INSERT INTO RouteSpecification(origin, destination, arrivalDeadline) " +
                                "VALUES(" +
                                "(SELECT id FROM Location WHERE unLocode = :origin), " +
                                "(SELECT id FROM Location WHERE unLocode = :destination), " +
                                ":arrivalDeadline)")
                        .bindMap(ImmutableMap.of(
                                "origin", routeSpec[0],
                                "destination", routeSpec[1],
                                "arrivalDeadline", routeSpec[2]
                        ))
                        .execute();
                routeSpecIds.add(h.createQuery("CALL IDENTITY()").mapTo(Integer.class).findOnly());
            }
        });

        String cargoSql =
                "insert into Cargo (trackingId, origin, routeSpecification, transportStatus, lastKnownLocation, misdirected, routingStatus, calculatedAt, isUnloadedAtDestination) " +
                        "values (:trackingId, (SELECT id FROM Location WHERE unLocode = :originId), :routeSpecId, :transportStatus, (SELECT id FROM Location WHERE unLocode = :lastKnownLocationId), :isMisdirected, :routingStatus, :calculatedAt, :unloadedAtDest)";

        String[] keys = {
                "trackingId", "originId", "routeSpecId", "transportStatus", "lastKnownLocationId", "isMisdirected", "routingStatus", "calculatedAt", "unloadedAtDest"
        };
        Object[][] cargoArgs = {
                {"XYZ", "SESTO", routeSpecIds.get(0), "IN_PORT", "SESTO", false, "ROUTED", ts(100), false},
                {"ABC", "SESTO", routeSpecIds.get(1), "IN_PORT", "SESTO", false, "ROUTED", ts(100), false},
                {"ZYX", "AUMEL", routeSpecIds.get(2), "IN_PORT", "SESTO", false, "NOT_ROUTED", ts(100), false},
                {"CBA", "FIHEL", routeSpecIds.get(3), "IN_PORT", "SESTO", false, "MISROUTED", ts(100), false},
                {"FGH", "SESTO", routeSpecIds.get(4), "IN_PORT", "SESTO", false, "ROUTED", ts(100), false},  // Cargo origin differs from spec origin
                {"JKL", "DEHAM", routeSpecIds.get(5), "IN_PORT", "SESTO", true, "ROUTED", ts(100), false},
        };
        jdbi.useHandle(h -> {
            for (Object[] cargo : cargoArgs) {
                h.createUpdate(cargoSql)
                        .bindMap(joinToMap(keys, cargo))
                        .execute();
            }
        });
    }

    public static void loadCarrierMovementData(Jdbi jdbi) {
        String voyageSql =
                "insert into Voyage (voyageNumber) values (:voyageNo)";
        String[] keys = {"voyageNo"};
        Object[][] voyageArgs = {
                {"0101"},
                {"0202"},
                {"0303"}
        };
        List<Integer> voyageIds = new ArrayList<>();
        jdbi.useHandle(h -> {
            for (Object[] voyage : voyageArgs) {
                h.createUpdate(voyageSql)
                        .bindMap(joinToMap(keys, voyage))
                        .execute();
                int voyageId = h.createQuery("CALL IDENTITY()").mapTo(Integer.class).findOnly();
                voyageIds.add(voyageId);
            }
        });

        String carrierMovementSql =
                "insert into CarrierMovement (voyage, departureLocation, arrivalLocation, departureTime, arrivalTime) " +
                        "values (:voyageId," +
                        "(SELECT id FROM Location WHERE unLocode = :departureLocationId)," +
                        "(SELECT id FROM Location WHERE unLocode = :arrivalLocationId)," +
                        ":departureTime," +
                        ":arrivalTime)";

        String[] cmKeys = {
                "voyageId","departureLocationId","arrivalLocationId","departureTime","arrivalTime"
        };
        Object[][] carrierMovementArgs = {
                // SESTO - FIHEL - DEHAM - CNHKG - JPTOK - AUMEL (voyage 0101)
                {voyageIds.get(0), "SESTO", "FIHEL", ts(1), ts(2)},
                {voyageIds.get(0), "FIHEL", "DEHAM", ts(1), ts(2)},
                {voyageIds.get(0), "DEHAM", "CNHKG", ts(1), ts(2)},
                {voyageIds.get(0), "CNHKG", "JPTOK", ts(1), ts(2)},
                {voyageIds.get(0), "JPTOK", "AUMEL", ts(1), ts(2)},

                // AUMEL - USCHI - DEHAM - SESTO - FIHEL (voyage 0202)
                {voyageIds.get(1), "AUMEL", "USCHI", ts(1), ts(2)},
                {voyageIds.get(1), "USCHI", "DEHAM", ts(1), ts(2)},
                {voyageIds.get(1), "DEHAM", "SESTO", ts(1), ts(2)},
                {voyageIds.get(1), "SESTO", "FIHEL", ts(1), ts(2)},

                // CNHKG - AUMEL - FIHEL - DEHAM - SESTO - USCHI - JPTKO (voyage 0303)
                {voyageIds.get(2), "CNHKG", "AUMEL", ts(1), ts(2)},
                {voyageIds.get(2), "AUMEL", "FIHEL", ts(1), ts(2)},
                {voyageIds.get(2), "DEHAM", "SESTO", ts(1), ts(2)},
                {voyageIds.get(2), "SESTO", "USCHI", ts(1), ts(2)},
                {voyageIds.get(2), "USCHI", "JPTOK", ts(1), ts(2)}
        };
        jdbi.useHandle(h -> {
            for (Object[] carrierMovement : carrierMovementArgs) {
                h.createUpdate(carrierMovementSql)
                        .bindMap(joinToMap(cmKeys, carrierMovement))
                        .execute();
            }
        });
    }

    public static void loadLocationData(Jdbi jdbi) {
        String locationSql =
                "insert into Location (unlocode, name) " +
                        "values (:unloCode, :name)";

        String[] keys = {"unloCode", "name"};
        Object[][] locationArgs = {
                {"SESTO", "Stockholm"},
                {"AUMEL", "Melbourne"},
                {"CNHKG", "Hongkong"},
                {"JPTOK", "Tokyo"},
                {"FIHEL", "Helsinki"},
                {"DEHAM", "Hamburg"},
                {"USCHI", "Chicago"}
        };
        jdbi.useHandle(h -> {
            for (Object[] location : locationArgs) {
                h.createUpdate(locationSql).bindMap(joinToMap(keys, location)).execute();
            }
        });
    }

    private static Timestamp ts(int hours) {
        return new Timestamp(base.getTime() + 1000L * 60 * 60 * hours);
    }

    public static Date offset(int hours) {
        return new Date(ts(hours).getTime());
    }

    private static Map<String, Object> joinToMap(String[] keys, Object[] values) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            if (value == null) {
                value = new NullArgument(Types.NULL);
            }
            map.put(keys[i], value);
        }
        return map;
    }
}
