package se.citerus.dddsample.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.domain.HandlingEvent.Type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class HandlingEventRepositoryInMem implements HandlingEventRepository{
  private HashMap<String, HandlingEvent> eventDB;
  private CarrierMovementRepository carrierMovementRepository;
  
  private final Log logger = LogFactory.getLog(getClass());

  public HandlingEventRepositoryInMem() throws ParseException {
    eventDB = new HashMap<String, HandlingEvent>();
  }

  /**
   * Initilaze the in mem repository.
   * 
   * SpringIoC will call this init-method after the bean has bean created and properties has been set.
   * 
   * @throws ParseException
   */
  public void init() throws ParseException {
    
    // CargoXYZ
    final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));
    registerEvent(cargoXYZ, "2007-11-30", HandlingEvent.Type.RECEIVE, null);

    final CarrierMovement stockholmToHamburg = carrierMovementRepository.find(new CarrierId("SESTO_DEHAM"));
    registerEvent(cargoXYZ, "2007-12-01", HandlingEvent.Type.LOAD, stockholmToHamburg);
    registerEvent(cargoXYZ, "2007-12-02", HandlingEvent.Type.UNLOAD, stockholmToHamburg);
    
    final CarrierMovement hamburgToHongKong = carrierMovementRepository.find(new CarrierId("DEHAM_CNHKG"));
    registerEvent(cargoXYZ, "2007-12-03", HandlingEvent.Type.LOAD, hamburgToHongKong);
    registerEvent(cargoXYZ, "2007-12-05", HandlingEvent.Type.UNLOAD, hamburgToHongKong);
    
    //CargoZYX
    final Cargo cargoZYX = new Cargo(new TrackingId("ZYX"), new Location("AUMEL"), new Location("SESTO"));
    registerEvent(cargoZYX, "2007-12-09", HandlingEvent.Type.RECEIVE, null);
    
    final CarrierMovement melbourneToTokyo = carrierMovementRepository.find(new CarrierId("AUMEL_JPTOK"));
    registerEvent(cargoZYX, "2007-12-10", HandlingEvent.Type.LOAD, melbourneToTokyo);
    registerEvent(cargoZYX, "2007-12-12", HandlingEvent.Type.UNLOAD, melbourneToTokyo);
    
    final CarrierMovement tokyoToLosAngeles = carrierMovementRepository.find(new CarrierId("JPTOK_USLA"));
    registerEvent(cargoZYX, "2007-12-13", HandlingEvent.Type.LOAD, tokyoToLosAngeles);
 
    //CargoABC
    final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new Location("SESTO"), new Location("FIHEL"));
    registerEvent(cargoABC, "2008-01-01", HandlingEvent.Type.RECEIVE, null);
    
    final CarrierMovement stockholmToHelsinki = new CarrierMovement(
            new CarrierId("CAR_001"), new Location("SESTO"), new Location("FIHEL"));

    registerEvent(cargoABC, "2008-01-02", HandlingEvent.Type.LOAD, stockholmToHelsinki);
    registerEvent(cargoABC, "2008-01-03", HandlingEvent.Type.UNLOAD, stockholmToHelsinki);
    registerEvent(cargoABC, "2008-01-05", HandlingEvent.Type.CLAIM, null);

    //CargoCBA
    final Cargo cargoCBA = new Cargo(new TrackingId("CBA"), new Location("FIHEL"), new Location("SESTO"));
    registerEvent(cargoCBA, "2008-01-10", HandlingEvent.Type.RECEIVE, null);
  }

  
  private void registerEvent(Cargo cargo, String date, Type type, CarrierMovement carrierMovement) throws ParseException{
    HandlingEvent ev= new HandlingEvent(getDate(date), type, carrierMovement);
    ev.register(toSet(cargo));
    String id = cargo.trackingId() + "_" + type + "_" + date;
    
    logger.debug("Adding event " + id + "(" + ev + ")");
    eventDB.put(id, ev);
  }
  
  
  private Set<Cargo> toSet(Cargo... cargoArgs) {
    Set<Cargo> cargos = new HashSet<Cargo>();
    for (Cargo cargo : cargoArgs) {
      cargos.add(cargo);
    }
    
    return cargos;
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
  
  public HandlingEvent find(String handlingEventId){
    return eventDB.get(handlingEventId);
  }

  public void save(HandlingEvent event) {
    // Mimmick saving to database
    for (Cargo cargo : event.getRegisterdCargos()) {
      cargo.handle(event);
    }
  }

  @SuppressWarnings("unchecked")
  public Set<HandlingEvent> findByTrackingId(final TrackingId trackingId) {
    Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    for (HandlingEvent event : eventDB.values()) {
      for (Cargo cargo : event.getRegisterdCargos()) {
        if (cargo.trackingId().equals(trackingId)) {
          events.add(event);
          break;
        }
      }   
    }
    
    logger.debug("findByTrackingId " + trackingId + " finds " + events);
    
    return events;
  }

  public void setCarrierRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
