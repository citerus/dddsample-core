package se.citerus.dddsample.repository;

import org.springframework.dao.DataRetrievalFailureException;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.DeliveryHistory;
import se.citerus.dddsample.domain.Itinerary;
import static se.citerus.dddsample.domain.SampleLocations.*;
import se.citerus.dddsample.domain.TrackingId;

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
    //No need to save anything with InMem
  }

  public TrackingId nextTrackingId() {
    String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }

  public void deleteItinerary(Itinerary itinerary) {
  }

  public List<Cargo> findAll() {
    return new ArrayList(cargoDb.values());
  }

  public void init() throws Exception {
    String trackIdXYZ = "XYZ";
    final Cargo cargoXYZ = new Cargo(new TrackingId(trackIdXYZ), STOCKHOLM, MELBOURNE);
    cargoDb.put(trackIdXYZ, cargoXYZ);
    
    String trackIdZYX = "ZYX";
    final Cargo cargoZYX = new Cargo(new TrackingId(trackIdZYX), MELBOURNE, STOCKHOLM);
    cargoDb.put(trackIdZYX, cargoZYX);
    
    String trackIdABC = "ABC";
    final Cargo cargoABC = new Cargo(new TrackingId(trackIdABC), STOCKHOLM, HELSINKI);
    cargoDb.put(trackIdABC, cargoABC);
    
    String trackIdCBA = "CBA";
    final Cargo cargoCBA = new Cargo(new TrackingId(trackIdCBA), HELSINKI, STOCKHOLM);
    cargoDb.put(trackIdCBA, cargoCBA);

    for (Cargo cargo : cargoDb.values()) {
      DeliveryHistory dh = new DeliveryHistory(handlingEventRepository.findEventsForCargo(cargo.trackingId()));
      cargo.setDeliveryHistory(dh);
    }
  }

  public void setHandlingEventRepository(final HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
