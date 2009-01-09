package se.citerus.dddsample.infrastructure.persistence.inmemory;

import org.springframework.dao.DataRetrievalFailureException;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.CargoTestHelper;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.util.*;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * <p/>
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * <p/>
 * To be able to test exceptions, a DataRetrievalFailureException is thrown when finding a Cargo with trackingId "DAE".
 */
public class CargoRepositoryInMem implements CargoRepository {

  private Map<String, Cargo> cargoDb;
  private HandlingEventRepository handlingEventRepository;

  /**
   * Constructor.
   */
  public CargoRepositoryInMem() {
    cargoDb = new HashMap<String, Cargo>();
  }

  public Cargo find(final TrackingId trackingId) {
    if (trackingId.idString().equalsIgnoreCase("DAE")) {
      throw new DataRetrievalFailureException("Network failure. Please try again");
    }

    return cargoDb.get(trackingId.idString());
  }

  public void save(final Cargo cargo) {
    cargoDb.put(cargo.trackingId().idString(), cargo);
  }

  public TrackingId nextTrackingId() {
    String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }

  public List<Cargo> findAll() {
    return new ArrayList(cargoDb.values());
  }

  public void init() throws Exception {
    final TrackingId xyz = new TrackingId("XYZ");
    final Cargo cargoXYZ = CargoTestHelper.createCargoWithDeliveryHistory(
      xyz, STOCKHOLM, MELBOURNE, handlingEventRepository.findEventsForCargo(xyz));
    cargoDb.put(xyz.idString(), cargoXYZ);

    final TrackingId zyx = new TrackingId("ZYX");
    final Cargo cargoZYX = CargoTestHelper.createCargoWithDeliveryHistory(
      zyx, MELBOURNE, STOCKHOLM, handlingEventRepository.findEventsForCargo(zyx));
    cargoDb.put(zyx.idString(), cargoZYX);

    final TrackingId abc = new TrackingId("ABC");
    final Cargo cargoABC = CargoTestHelper.createCargoWithDeliveryHistory(
      abc, STOCKHOLM, HELSINKI, handlingEventRepository.findEventsForCargo(abc));
    cargoDb.put(abc.idString(), cargoABC);

    final TrackingId cba = new TrackingId("CBA");
    final Cargo cargoCBA = CargoTestHelper.createCargoWithDeliveryHistory(
      cba, HELSINKI, STOCKHOLM, handlingEventRepository.findEventsForCargo(cba));
    cargoDb.put(cba.idString(), cargoCBA);
  }

  public void setHandlingEventRepository(final HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
