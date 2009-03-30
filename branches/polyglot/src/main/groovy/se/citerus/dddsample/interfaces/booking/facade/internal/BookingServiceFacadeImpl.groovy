package se.citerus.dddsample.interfaces.booking.facade.internal

import org.apache.log4j.Logger
import se.citerus.dddsample.application.BookingService
import se.citerus.dddsample.domain.model.cargo.CargoRepository
import se.citerus.dddsample.domain.model.cargo.Itinerary
import se.citerus.dddsample.domain.model.cargo.TrackingId
import se.citerus.dddsample.domain.model.location.LocationRepository
import se.citerus.dddsample.domain.model.location.UnLocode
import se.citerus.dddsample.domain.model.voyage.VoyageRepository
import se.citerus.dddsample.interfaces.booking.facade.BookingServiceFacade
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO
import se.citerus.dddsample.interfaces.booking.facade.internal.DTOAssembly

/**
 * This implementation has additional support from the infrastructure, for exposing as an RMI
 * service and for keeping the OR-mapper unit-of-work open during DTO assembly,
 * analogous to the view rendering for web interfaces.
 *
 */
class BookingServiceFacadeImpl implements BookingServiceFacade {

  BookingService bookingService
  LocationRepository locationRepository
  CargoRepository cargoRepository
  VoyageRepository voyageRepository

  BookingServiceFacadeImpl() {
    DTOAssembly.applyMixins()
  }

  @Override
  List<LocationDTO> listShippingLocations() {
    def allLocations = locationRepository.findAll()
    return allLocations.collect { it.toDTO() }
  }

  @Override
  String bookNewCargo(String origin, String destination, Date arrivalDeadline) {
    def trackingId = bookingService.bookNewCargo(
      new UnLocode(origin), 
      new UnLocode(destination),
      arrivalDeadline
    )
    return trackingId.idString()
  }

  @Override
  CargoRoutingDTO loadCargoForRouting(String trackingId) {
    def cargo = cargoRepository.find(new TrackingId(trackingId))
    return cargo.toDTO()
  }

  @Override
  void assignCargoToRoute(String trackingIdStr, RouteCandidateDTO routeCandidateDTO) {
    def itinerary = Itinerary.fromDTO(routeCandidateDTO, voyageRepository, locationRepository)
    def trackingId = new TrackingId(trackingIdStr)

    bookingService.assignCargoToRoute(itinerary, trackingId)
  }

  @Override
  void changeDestination(String trackingId, String destinationUnLocode) {
    bookingService.changeDestination(new TrackingId(trackingId), new UnLocode(destinationUnLocode))
  }

  @Override
  List<CargoRoutingDTO> listAllCargos() {
    def allCargos = cargoRepository.findAll()
    return allCargos.collect { it.toDTO() }
  }

  @Override
  List<RouteCandidateDTO> requestPossibleRoutesForCargo(String trackingId) {
    def itineraries = bookingService.requestPossibleRoutesForCargo(new TrackingId(trackingId))
    return itineraries.collect { it.toDTO() }
  }

}
