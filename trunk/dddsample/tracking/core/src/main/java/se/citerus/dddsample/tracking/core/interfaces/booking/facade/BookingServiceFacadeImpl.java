package se.citerus.dddsample.tracking.core.interfaces.booking.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.citerus.dddsample.tracking.booking.api.*;
import se.citerus.dddsample.tracking.core.application.booking.BookingService;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Itinerary;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static se.citerus.dddsample.tracking.core.interfaces.booking.facade.DTOAssembler.*;


/**
 * This implementation has additional support from the infrastructure, for exposing as an RMI
 * service and for keeping the OR-mapper unit-of-work open during DTO assembly,
 * analogous to the view rendering for web interfaces.
 */
@Service
public class BookingServiceFacadeImpl implements BookingServiceFacade {

  private final BookingService bookingService;
  private final LocationRepository locationRepository;
  private final CargoRepository cargoRepository;
  private final VoyageRepository voyageRepository;

  @Autowired
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

  @Override
  public List<VoyageDTO> listAllVoyages() {
    // TODO
    List<VoyageDTO> voyages = new ArrayList<VoyageDTO>();

    voyages.add(new VoyageDTO("V0100", Arrays.asList(
      new CarrierMovementDTO(new LocationDTO("CNHKG", "Hongkong"), new LocationDTO("USLBG", "Long Beach")),
      new CarrierMovementDTO(new LocationDTO("USLBG", "Long Beach"), new LocationDTO("USDAL", "Dallas")),
      new CarrierMovementDTO(new LocationDTO("USDAL", "Dallas"), new LocationDTO("CAOTT", "Ottawa"))
    )));
    voyages.add(new VoyageDTO("V0200", Arrays.asList(
      new CarrierMovementDTO(new LocationDTO("USLBG", "Long Beach"), new LocationDTO("USDAL", "Dallas")),
      new CarrierMovementDTO(new LocationDTO("CNHKG", "Hongkong"), new LocationDTO("USLBG", "Long Beach")),
      new CarrierMovementDTO(new LocationDTO("USDAL", "Dallas"), new LocationDTO("CAOTT", "Ottawa"))
    )));

    return voyages;
  }

  @Override
  public void departureDelayed(VoyageDelayDTO delay) {
    // TODO
  }

  @Override
  public void arrivalDelayed(VoyageDelayDTO delay) {
    // TODO
  }

}
