package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

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

  public CargoRepositoryInMem() throws Exception {
    cargoDb = new HashMap<String, Cargo>();
    setup();
  }

  public Cargo find(TrackingId trackingId) {
    if (trackingId.getId().equalsIgnoreCase("DAE")){
      throw new DataRetrievalFailureException("Network failure. Please try again");
    }
    
    return cargoDb.get(trackingId.getId());
  }

  private void setup() throws Exception {
    String trackIdXYZ = "XYZ";
    final Cargo cargoXYZ = new Cargo(new TrackingId(trackIdXYZ), new Location("SESTO"), new Location("AUMEL"));
    
    cargoXYZ.handle(new HandlingEvent(getDate("2007-11-30"), HandlingEvent.Type.RECEIVE, null));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-01"), HandlingEvent.Type.LOAD, stockholmToHamburg));
    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-02"), HandlingEvent.Type.UNLOAD, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(new Location("DEHAM"), new Location("CNHKG"));

    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-03"), HandlingEvent.Type.LOAD, hamburgToHongKong));
    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-05"), HandlingEvent.Type.UNLOAD, hamburgToHongKong));

    cargoDb.put(trackIdXYZ, cargoXYZ);
    
    
    String trackIdZYX = "ZYX";
    final Cargo cargoZYX = new Cargo(new TrackingId(trackIdZYX), new Location("AUMEL"), new Location("SESTO"));
    
    cargoZYX.handle(new HandlingEvent(getDate("2007-12-09"), HandlingEvent.Type.RECEIVE, null));
    
    final CarrierMovement melbourneToTokyo = new CarrierMovement(new Location("AUMEL"), new Location("JPTOK"));

    cargoZYX.handle(new HandlingEvent(getDate("2007-12-10"), HandlingEvent.Type.LOAD, melbourneToTokyo));
    cargoZYX.handle(new HandlingEvent(getDate("2007-12-12"), HandlingEvent.Type.UNLOAD, melbourneToTokyo));

    final CarrierMovement tokyoToLosAngeles = new CarrierMovement(new Location("JPTOK"), new Location("USLA"));

    cargoZYX.handle(new HandlingEvent(getDate("2007-12-13"), HandlingEvent.Type.LOAD, tokyoToLosAngeles));

    cargoDb.put(trackIdZYX, cargoZYX);
    
    String trackIdABC = "ABC";
    final Cargo cargoABC = new Cargo(new TrackingId(trackIdABC), new Location("SESTO"), new Location("FIHEL"));
    
    cargoABC.handle(new HandlingEvent(getDate("2008-01-01"), HandlingEvent.Type.RECEIVE, null));

    final CarrierMovement stockholmToHelsinki = new CarrierMovement(new Location("SESTO"), new Location("FIHEL"));

    cargoABC.handle(new HandlingEvent(getDate("2008-01-02"), HandlingEvent.Type.LOAD, stockholmToHelsinki));
    cargoABC.handle(new HandlingEvent(getDate("2008-01-03"), HandlingEvent.Type.UNLOAD, stockholmToHelsinki));
    cargoABC.handle(new HandlingEvent(getDate("2008-01-05"), HandlingEvent.Type.CLAIM, null));
    
    cargoDb.put(trackIdABC, cargoABC);
    
    String trackIdCBA = "CBA";
    final Cargo cargoCBA = new Cargo(new TrackingId(trackIdCBA), new Location("FIHEL"), new Location("SESTO"));
    
    cargoCBA.handle(new HandlingEvent(getDate("2008-01-10"), HandlingEvent.Type.RECEIVE, null));

    
    cargoDb.put(trackIdCBA, cargoCBA);
  }

  /**
   * Parse an ISO 8601 (YYYY-MM-DD) String to Date
   * 
   * @param isoFormat
   *            String to parse.
   * @return Created date instance.
   * @throws ParseException
   *             Thrown if parsing fails.
   */
  private Date getDate(String isoFormat) throws ParseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.parse(isoFormat);
  }
}
