package se.citerus.dddsample.infrastructure.persistence.inmemory;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;

import java.time.Instant;
import java.util.*;

import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * <p>
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * <p>
 */
public class CargoRepositoryInMem implements CargoRepository {

    private final Map<String, Cargo> cargoDb;
    private HandlingEventRepository handlingEventRepository;

    /**
     * Constructor.
     */
    public CargoRepositoryInMem() {
        cargoDb = new HashMap<>();
    }

    public Cargo find(final TrackingId trackingId) {
        return cargoDb.get(trackingId.idString());
    }

    public void store(final Cargo cargo) {
        cargoDb.put(cargo.trackingId().idString(), cargo);
    }

    public TrackingId nextTrackingId() {
        String random = UUID.randomUUID().toString().toUpperCase();
        return new TrackingId(
                random.substring(0, random.indexOf("-"))
        );
    }

    public List<Cargo> getAll() {
        return new ArrayList<>(cargoDb.values());
    }

    public void init() throws Exception {
        final TrackingId xyz = new TrackingId("XYZ");
        final Cargo cargoXYZ = createCargoWithDeliveryHistory(
                xyz, STOCKHOLM, MELBOURNE, handlingEventRepository.lookupHandlingHistoryOfCargo(xyz));
        cargoDb.put(xyz.idString(), cargoXYZ);

        final TrackingId zyx = new TrackingId("ZYX");
        final Cargo cargoZYX = createCargoWithDeliveryHistory(
                zyx, MELBOURNE, STOCKHOLM, handlingEventRepository.lookupHandlingHistoryOfCargo(zyx));
        cargoDb.put(zyx.idString(), cargoZYX);

        final TrackingId abc = new TrackingId("ABC");
        final Cargo cargoABC = createCargoWithDeliveryHistory(
                abc, STOCKHOLM, HELSINKI, handlingEventRepository.lookupHandlingHistoryOfCargo(abc));
        cargoDb.put(abc.idString(), cargoABC);

        final TrackingId cba = new TrackingId("CBA");
        final Cargo cargoCBA = createCargoWithDeliveryHistory(
                cba, HELSINKI, STOCKHOLM, handlingEventRepository.lookupHandlingHistoryOfCargo(cba));
        cargoDb.put(cba.idString(), cargoCBA);
    }

    public void setHandlingEventRepository(final HandlingEventRepository handlingEventRepository) {
        this.handlingEventRepository = handlingEventRepository;
    }

    public static Cargo createCargoWithDeliveryHistory(TrackingId trackingId,
                                                       Location origin,
                                                       Location destination,
                                                       HandlingHistory handlingHistory) {

        final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, Instant.now());
        final Cargo cargo = new Cargo(trackingId, routeSpecification);
        cargo.deriveDeliveryProgress(handlingHistory);

        return cargo;
    }
}
