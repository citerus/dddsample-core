package se.citerus.dddsample.repository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import se.citerus.dddsample.domain.*;
import se.citerus.dddsample.domain.HandlingEvent.Type;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class HandlingEventRepositoryInMem implements HandlingEventRepository {
  private final Log logger = LogFactory.getLog(getClass());
  private final HashMap<String, HandlingEvent> eventDB = new HashMap<String, HandlingEvent>();
  private CarrierMovementRepository carrierMovementRepository;

  /**
   * Initilaze the in mem repository.
   * <p/>
   * SpringIoC will call this init-method after the bean has bean created and properties has been set.
   *
   * @throws ParseException
   */
  public void init() throws ParseException {
/*

    // CargoXYZ
    final Cargo cargoXYZ = new Cargo(new TrackingId("XYZ"), new Location("SESTO"), new Location("AUMEL"));
    registerEvent(cargoXYZ, "2007-11-30", HandlingEvent.Type.RECEIVE, null);

    final CarrierMovement stockholmToHamburg = carrierMovementRepository.find(new CarrierMovementId("SESTO_DEHAM"));
    registerEvent(cargoXYZ, "2007-12-01", HandlingEvent.Type.LOAD, stockholmToHamburg);
    registerEvent(cargoXYZ, "2007-12-02", HandlingEvent.Type.UNLOAD, stockholmToHamburg);
    
    final CarrierMovement hamburgToHongKong = carrierMovementRepository.find(new CarrierMovementId("DEHAM_CNHKG"));
    registerEvent(cargoXYZ, "2007-12-03", HandlingEvent.Type.LOAD, hamburgToHongKong);
    registerEvent(cargoXYZ, "2007-12-05", HandlingEvent.Type.UNLOAD, hamburgToHongKong);
    
    //CargoZYX
    final Cargo cargoZYX = new Cargo(new TrackingId("ZYX"), new Location("AUMEL"), new Location("SESTO"));
    registerEvent(cargoZYX, "2007-12-09", HandlingEvent.Type.RECEIVE, null);
    
    final CarrierMovement melbourneToTokyo = carrierMovementRepository.find(new CarrierMovementId("AUMEL_JPTOK"));
    registerEvent(cargoZYX, "2007-12-10", HandlingEvent.Type.LOAD, melbourneToTokyo);
    registerEvent(cargoZYX, "2007-12-12", HandlingEvent.Type.UNLOAD, melbourneToTokyo);
    
    final CarrierMovement tokyoToLosAngeles = carrierMovementRepository.find(new CarrierMovementId("JPTOK_USLA"));
    registerEvent(cargoZYX, "2007-12-13", HandlingEvent.Type.LOAD, tokyoToLosAngeles);
 
    //CargoABC
    final Cargo cargoABC = new Cargo(new TrackingId("ABC"), new Location("SESTO"), new Location("FIHEL"));
    registerEvent(cargoABC, "2008-01-01", HandlingEvent.Type.RECEIVE, null);
    
    final CarrierMovement stockholmToHelsinki = new CarrierMovement(
            new CarrierMovementId("CAR_001"), new Location("SESTO"), new Location("FIHEL"));

    registerEvent(cargoABC, "2008-01-02", HandlingEvent.Type.LOAD, stockholmToHelsinki);
    registerEvent(cargoABC, "2008-01-03", HandlingEvent.Type.UNLOAD, stockholmToHelsinki);
    registerEvent(cargoABC, "2008-01-05", HandlingEvent.Type.CLAIM, null);

    //CargoCBA
    final Cargo cargoCBA = new Cargo(new TrackingId("CBA"), new Location("FIHEL"), new Location("SESTO"));
    registerEvent(cargoCBA, "2008-01-10", HandlingEvent.Type.RECEIVE, null);
*/
  }

  private void registerEvent(Cargo cargo, String date, Type type, CarrierMovement carrierMovement) throws ParseException {
    HandlingEvent event = new HandlingEvent(cargo, getDate(date), new Date(), type, null, carrierMovement);
    //cargo.handle(event);
    String id = cargo.trackingId() + "_" + type + "_" + date;

    logger.debug("Adding event " + id + "(" + event + ")");
    eventDB.put(id, event);
  }


  /**
   * Parse an ISO 8601 (YYYY-MM-DD) String to Date
   *
   * @param isoFormat String to parse.
   * @return Created date instance.
   * @throws ParseException Thrown if parsing fails.
   */
  private Date getDate(final String isoFormat) throws ParseException {
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    return dateFormat.parse(isoFormat);
  }

  public HandlingEvent find(final String handlingEventId) {
    return eventDB.get(handlingEventId);
  }

  public void save(final HandlingEvent event) {
    // Mimmick saving to database
    /*
    for (Cargo cargo : event.registerdCargos()) {
      cargo.handle(event);
    }
    */
  }

  public List<HandlingEvent> findEventsForCargo(final TrackingId trackingId) {
    return new ArrayList<HandlingEvent>(eventDB.values());
  }

  public DeliveryHistory findDeliveryHistory(final TrackingId trackingId) {
    return null;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @SuppressWarnings("unchecked")
  public Set<HandlingEvent> findByTrackingId(final TrackingId trackingId) {
    final Set<HandlingEvent> events = new HashSet<HandlingEvent>();
    for (HandlingEvent event : eventDB.values()) {
      /*
      for (Cargo cargo : event.registerdCargos()) {
        if (cargo.trackingId().equals(trackingId)) {
          events.add(event);
          break;
        }
      }
        */
    }

    logger.debug("findByTrackingId " + trackingId + " finds " + events);

    return events;
  }

  public void setCarrierRepository(final CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }
}
