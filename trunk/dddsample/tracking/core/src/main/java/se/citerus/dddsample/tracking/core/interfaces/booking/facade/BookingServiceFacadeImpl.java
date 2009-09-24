package se.citerus.dddsample.tracking.core.interfaces.booking.facade;

import se.citerus.dddsample.tracking.core.application.booking.BookingService;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Itinerary;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;
import static se.citerus.dddsample.tracking.core.interfaces.booking.facade.DTOAssembler.*;
import se.citerus.dddsample.tracking.booking.api.BookingServiceFacade;
import se.citerus.dddsample.tracking.booking.api.CargoRoutingDTO;
import se.citerus.dddsample.tracking.booking.api.LocationDTO;
import se.citerus.dddsample.tracking.booking.api.RouteCandidateDTO;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;


/**
 * This implementation has additional support from the infrastructure, for exposing as an RMI
 * service and for keeping the OR-mapper unit-of-work open during DTO assembly,
 * analogous to the view rendering for web interfaces.
 */
public class BookingServiceFacadeImpl implements BookingServiceFacade {

  private final BookingService bookingService;
  private final LocationRepository locationRepository;
  private final CargoRepository cargoRepository;
  private final VoyageRepository voyageRepository;

  public BookingServiceFacadeImpl(final BookingService bookingService, final LocationRepository locationRepository,
                                  final CargoRepository cargoRepository, final VoyageRepository voyageRepository) {
    this.bookingService = bookingService;
    this.locationRepository = locationRepository;
    this.cargoRepository = cargoRepository;
    this.voyageRepository = voyageRepository;
  }

  @Override
  public List<LocationDTO> listShippingLocations() {
    final List<Location> allLocations = locationRepository.findAll();
    return toDTOList(allLocations);
  }

  @Override
  public String bookNewCargo(String origin, String destination, Date arrivalDeadline) {
    TrackingId trackingId = bookingService.bookNewCargo(
      new UnLocode(origin),
      new UnLocode(destination),
      arrivalDeadline
    );
    return trackingId.stringValue();
  }

  @Override
  public CargoRoutingDTO loadCargoForRouting(String trackingId) {
    final Cargo cargo = bookingService.loadCargoForRouting(new TrackingId(trackingId));
    return toDTO(cargo);
  }

  @Override
  public void assignCargoToRoute(String trackingIdStr, RouteCandidateDTO routeCandidateDTO) {
    final Itinerary itinerary = fromDTO(routeCandidateDTO, voyageRepository, locationRepository);
    final TrackingId trackingId = new TrackingId(trackingIdStr);

    bookingService.assignCargoToRoute(itinerary, trackingId);
  }

  @Override
  public void changeDestination(String trackingId, String destinationUnLocode) throws RemoteException {
    bookingService.changeDestination(new TrackingId(trackingId), new UnLocode(destinationUnLocode));
  }

  @Override
  public List<CargoRoutingDTO> listAllCargos() {
    final List<Cargo> cargoList = cargoRepository.findAll();
    final List<CargoRoutingDTO> dtoList = new ArrayList<CargoRoutingDTO>(cargoList.size());
    for (Cargo cargo : cargoList) {
      dtoList.add(toDTO(cargo));
    }
    return dtoList;
  }

  @Override
  public List<RouteCandidateDTO> requestPossibleRoutesForCargo(String trackingId) throws RemoteException {
    final List<Itinerary> itineraries = bookingService.requestPossibleRoutesForCargo(new TrackingId(trackingId));

    final List<RouteCandidateDTO> routeCandidates = new ArrayList<RouteCandidateDTO>(itineraries.size());
    for (Itinerary itinerary : itineraries) {
      routeCandidates.add(toDTO(itinerary));
    }

    return routeCandidates;
  }

}
