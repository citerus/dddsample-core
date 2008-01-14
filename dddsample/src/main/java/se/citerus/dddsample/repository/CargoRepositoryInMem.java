package se.citerus.dddsample.repository;

import org.springframework.dao.DataRetrievalFailureException;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;

import java.util.HashMap;
import java.util.Map;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * 
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * 
 * To be able to test exceptions, a DataRetrievalFailureException is thrown when finding a Cargo with trackingId "DAE".
 * 
 */
public class CargoRepositoryInMem implements CargoRepository {
  private Map<String, Cargo> cargoDb;
  private HandlingEventRepository handlingEventRepository;

  public CargoRepositoryInMem() throws Exception {
    cargoDb = new HashMap<String, Cargo>();
  }

  public Cargo find(TrackingId trackingId) {
    if (trackingId.toString().equalsIgnoreCase("DAE")){
      throw new DataRetrievalFailureException("Network failure. Please try again");
    }
    
    return cargoDb.get(trackingId.toString());
  }
  
  public void save(Cargo cargo) {
    //No need to save anything with InMem
  }

  /**
   * 
   * @throws Exception
   */
  public void init() throws Exception {
    String trackIdXYZ = "XYZ";
    final Cargo cargoXYZ = new Cargo(new TrackingId(trackIdXYZ), new Location("SESTO"), new Location("AUMEL"));
    cargoDb.put(trackIdXYZ, cargoXYZ);
    
    String trackIdZYX = "ZYX";
    final Cargo cargoZYX = new Cargo(new TrackingId(trackIdZYX), new Location("AUMEL"), new Location("SESTO"));
    cargoDb.put(trackIdZYX, cargoZYX);
    
    String trackIdABC = "ABC";
    final Cargo cargoABC = new Cargo(new TrackingId(trackIdABC), new Location("SESTO"), new Location("FIHEL"));
    cargoDb.put(trackIdABC, cargoABC);
    
    String trackIdCBA = "CBA";
    final Cargo cargoCBA = new Cargo(new TrackingId(trackIdCBA), new Location("FIHEL"), new Location("SESTO"));
    cargoDb.put(trackIdCBA, cargoCBA);
    
    for (Cargo cargo : cargoDb.values()) {
      for (HandlingEvent event : handlingEventRepository.findByTrackingId(cargo.trackingId())) {
        cargo.handle(event);
      }   
    }
  }

  public void setHandlingEventRepository(HandlingEventRepository handlingEventRepository) {
    this.handlingEventRepository = handlingEventRepository;
  }
}
