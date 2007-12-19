package se.citerus.dddsample.repository;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CargoRepository;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.HandlingEvent;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.TrackingId;

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
