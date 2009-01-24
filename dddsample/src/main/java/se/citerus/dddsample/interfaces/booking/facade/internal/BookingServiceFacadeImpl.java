package se.citerus.dddsample.interfaces.booking.facade.internal;

import org.apache.log4j.Logger;
import se.citerus.dddsample.application.BookingService;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.CargoRoutingDTOAssembler;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.ItineraryCandidateDTOAssembler;
import se.citerus.dddsample.interfaces.booking.facade.internal.assembler.LocationDTOAssembler;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * This implementation has additional support from the infrastructure, for exposing as an RMI
 * service and for keeping the OR-mapper unit-of-work open during DTO assembly,
 * analogous to the view rendering for web interfaces.
 *
 */
public class BookingServiceFacadeImpl implements BookingServiceFacade {

  private BookingService bookingService;
  private LocationRepository locationRepository;
  private CargoRepository cargoRepository;
  private VoyageRepository voyageRepository;
  private final Logger logger = Logger.getLogger(BookingServiceFacadeImpl.class);

  public List<LocationDTO> listShippingLocations() {
    final List<Location> allLocations = locationRepository.findAll();
    final LocationDTOAssembler assembler = new LocationDTOAssembler();
    return assembler.toDTOList(allLocations);
  }

  public String bookNewCargo(String origin, String destination, Date arrivalDeadline) {
    TrackingId trackingId = bookingService.bookNewCargo(
      new UnLocode(origin), 
      new UnLocode(destination),
      arrivalDeadline
    );
    return trackingId.idString();
  }

  public CargoRoutingDTO loadCargoForRouting(String trackingId) {
    final Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
    return assembler.toDTO(cargo);
  }

  public void assignCargoToRoute(String trackingId, ItineraryCandidateDTO itineraryCandidateDTO) {
    final Itinerary itinerary = new ItineraryCandidateDTOAssembler().fromDTO(itineraryCandidateDTO, voyageRepository, locationRepository);

    final Cargo cargo = cargoRepository.find(new TrackingId(trackingId));
    if (cargo == null) {
      throw new IllegalArgumentException("Can't assign itinerary to non-existing cargo " + trackingId);
    }

    cargo.assignToRoute(itinerary);
    cargoRepository.store(cargo);

    logger.info("Assigned cargo " + trackingId + " to new route");
  }

  public List<CargoRoutingDTO> listAllCargos() {
    final List<Cargo> cargoList = cargoRepository.findAll();
    final List<CargoRoutingDTO> dtoList = new ArrayList<CargoRoutingDTO>(cargoList.size());
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();
    for (Cargo cargo : cargoList) {
      dtoList.add(assembler.toDTO(cargo));
    }
    return dtoList;
  }

  public List<ItineraryCandidateDTO> requestPossibleRoutesForCargo(String trackingId) throws RemoteException {
    final List<Itinerary> itineraries = bookingService.requestPossibleRoutesForCargo(new TrackingId(trackingId));

    final List<ItineraryCandidateDTO> itineraryCandidates = new ArrayList<ItineraryCandidateDTO>(itineraries.size());
    final ItineraryCandidateDTOAssembler dtoAssembler = new ItineraryCandidateDTOAssembler();
    for (Itinerary itinerary : itineraries) {
      itineraryCandidates.add(dtoAssembler.toDTO(itinerary));
    }

    return itineraryCandidates;
  }

  public void setBookingService(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  public void setLocationRepository(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public void setCargoRepository(CargoRepository cargoRepository) {
    this.cargoRepository = cargoRepository;
  }

  public void setVoyageRepository(VoyageRepository voyageRepository) {
    this.voyageRepository = voyageRepository;
  }
}
