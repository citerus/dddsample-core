package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * CargoRepositoryInMem implement the CargoRepository interface but is a test
 * class not intended for usage in real application.
 * 
 * It setup a simple local hash with a number of Cargo's with TrackingId as key
 * defined at compile time.
 * 
 */
public class CargoRepositoryInMem implements CargoRepository {
  private Map<String, Cargo> cargoDb;

  public CargoRepositoryInMem() throws Exception {
    cargoDb = new HashMap<String, Cargo>();
    setup();
  }

  public Cargo find(TrackingId trackingId) {
    return cargoDb.get(trackingId.getId());
  }

  private void setup() throws Exception {
    String trackIdXYZ = "XYZ";
    final Cargo cargoXYZ = new Cargo(new TrackingId(trackIdXYZ), new Location("SESTO"), new Location("AUMEL"));

    final CarrierMovement stockholmToHamburg = new CarrierMovement(new Location("SESTO"), new Location("DEHAM"));

    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-01"), HandlingEvent.Type.ON, stockholmToHamburg));
    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-02"), HandlingEvent.Type.OFF, stockholmToHamburg));

    final CarrierMovement hamburgToHongKong = new CarrierMovement(new Location("DEHAM"), new Location("CNHKG"));

    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-03"), HandlingEvent.Type.ON, hamburgToHongKong));
    cargoXYZ.handle(new HandlingEvent(getDate("2007-12-05"), HandlingEvent.Type.OFF, hamburgToHongKong));

    cargoDb.put(trackIdXYZ, cargoXYZ);
    
    
    String trackIdZYX = "ZYX";
    final Cargo cargoZYX = new Cargo(new TrackingId(trackIdZYX), new Location("AUMEL"), new Location("SESTO"));

    final CarrierMovement melbourneToTokyo = new CarrierMovement(new Location("AUMEL"), new Location("JPTOK"));

    cargoZYX.handle(new HandlingEvent(getDate("2007-12-10"), HandlingEvent.Type.ON, melbourneToTokyo));
    cargoZYX.handle(new HandlingEvent(getDate("2007-12-12"), HandlingEvent.Type.OFF, melbourneToTokyo));

    final CarrierMovement tokyoToLosAngeles = new CarrierMovement(new Location("JPTOK"), new Location("USLA"));

    cargoZYX.handle(new HandlingEvent(getDate("2007-12-13"), HandlingEvent.Type.ON, tokyoToLosAngeles));

    cargoDb.put(trackIdZYX, cargoZYX);
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
