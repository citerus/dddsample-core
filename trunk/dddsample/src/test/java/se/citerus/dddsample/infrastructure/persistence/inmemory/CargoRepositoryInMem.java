package se.citerus.dddsample.infrastructure.persistence.inmemory;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingHistory;
import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.util.*;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * <p/>
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * <p/>
 */
public class CargoRepositoryInMem implements CargoRepository {

  private Map<String, Cargo> cargoDb;

  /**
   * Constructor.
   */
  public CargoRepositoryInMem() {
    cargoDb = new HashMap<String, Cargo>();
  }

  public Cargo find(final TrackingId trackingId) {
    return cargoDb.get(trackingId.stringValue());
  }

  public void store(final Cargo cargo) {
    cargoDb.put(cargo.trackingId().stringValue(), cargo);
  }

  public TrackingId nextTrackingId() {
    String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }

  public List<Cargo> findAll() {
    return new ArrayList<Cargo>(cargoDb.values());
  }

  public void init() throws Exception {
    final TrackingId xyz = new TrackingId("XYZ");
    final Cargo cargoXYZ = createCargoWithDeliveryHistory(xyz, STOCKHOLM, MELBOURNE);
    cargoDb.put(xyz.stringValue(), cargoXYZ);

    final TrackingId zyx = new TrackingId("ZYX");
    final Cargo cargoZYX = createCargoWithDeliveryHistory(zyx, MELBOURNE, STOCKHOLM);
    cargoDb.put(zyx.stringValue(), cargoZYX);

    final TrackingId abc = new TrackingId("ABC");
    final Cargo cargoABC = createCargoWithDeliveryHistory(abc, STOCKHOLM, HELSINKI);
    cargoDb.put(abc.stringValue(), cargoABC);

    final TrackingId cba = new TrackingId("CBA");
    final Cargo cargoCBA = createCargoWithDeliveryHistory(cba, HELSINKI, STOCKHOLM);
    cargoDb.put(cba.stringValue(), cargoCBA);
  }

  public static Cargo createCargoWithDeliveryHistory(TrackingId trackingId,
                                                     Location origin,
                                                     Location destination) {

    final RouteSpecification routeSpecification = new RouteSpecification(origin, destination, new Date());
    final Cargo cargo = new Cargo(trackingId, routeSpecification);
    cargo.deriveDeliveryProgress(HandlingHistory.emptyForCargo(cargo));

    return cargo;
  }
}
