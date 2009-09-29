package se.citerus.dddsample.tracking.core.interfaces.booking.facade;

import se.citerus.dddsample.tracking.booking.api.CargoRoutingDTO;
import se.citerus.dddsample.tracking.booking.api.LegDTO;
import se.citerus.dddsample.tracking.booking.api.LocationDTO;
import se.citerus.dddsample.tracking.booking.api.RouteCandidateDTO;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Itinerary;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Leg;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class DTOAssembler {

  /**
   * @param cargo cargo
   * @return A cargo routing DTO
   */
  static CargoRoutingDTO toDTO(final Cargo cargo) {
    final Itinerary itinerary = cargo.itinerary();

    List<LegDTO> legDTOList = Collections.emptyList();
    if (itinerary != null) {
      final List<Leg> legs = itinerary.legs();

      legDTOList = new ArrayList<LegDTO>(legs.size());
      for (Leg leg : legs) {
        final LegDTO legDTO = new LegDTO(
          leg.voyage().voyageNumber().stringValue(),
          leg.loadLocation().unLocode().stringValue(),
          leg.unloadLocation().unLocode().stringValue(),
          leg.loadTime(),
          leg.unloadTime());
        legDTOList.add(legDTO);
      }
    }

    return new CargoRoutingDTO(
      cargo.trackingId().stringValue(),
      cargo.routeSpecification().origin().unLocode().stringValue(),
      cargo.routeSpecification().destination().unLocode().stringValue(),
      cargo.routeSpecification().arrivalDeadline(),
      cargo.routingStatus().sameValueAs(RoutingStatus.MISROUTED),
      legDTOList
    );
  }

  /**
   * @param itinerary itinerary
   * @return A route candidate DTO
   */
  static RouteCandidateDTO toDTO(final Itinerary itinerary) {
    final List<LegDTO> legDTOs = new ArrayList<LegDTO>(itinerary.legs().size());
    for (Leg leg : itinerary.legs()) {
      legDTOs.add(toLegDTO(leg));
    }
    return new RouteCandidateDTO(legDTOs);
  }

  /**
   * @param leg leg
   * @return A leg DTO
   */
  static LegDTO toLegDTO(final Leg leg) {
    final VoyageNumber voyageNumber = leg.voyage().voyageNumber();
    final UnLocode from = leg.loadLocation().unLocode();
    final UnLocode to = leg.unloadLocation().unLocode();
    return new LegDTO(voyageNumber.stringValue(), from.stringValue(), to.stringValue(), leg.loadTime(), leg.unloadTime());
  }

  /**
   * @param routeCandidateDTO  route candidate DTO
   * @param voyageRepository   voyage repository
   * @param locationRepository location repository
   * @return An itinerary
   */
  static Itinerary fromDTO(final RouteCandidateDTO routeCandidateDTO,
                           final VoyageRepository voyageRepository,
                           final LocationRepository locationRepository) {
    final List<Leg> legs = new ArrayList<Leg>(routeCandidateDTO.getLegs().size());
    for (LegDTO legDTO : routeCandidateDTO.getLegs()) {
      final VoyageNumber voyageNumber = new VoyageNumber(legDTO.getVoyageNumber());
      final Voyage voyage = voyageRepository.find(voyageNumber);
      final Location from = locationRepository.find(new UnLocode(legDTO.getFrom()));
      final Location to = locationRepository.find(new UnLocode(legDTO.getTo()));
      legs.add(new Leg(voyage, from, to, legDTO.getLoadTime(), legDTO.getUnloadTime()));
    }
    return new Itinerary(legs);
  }

  static LocationDTO toDTO(final Location location) {
    return new LocationDTO(location.unLocode().stringValue(), location.name());
  }

  static List<LocationDTO> toDTOList(final List<Location> allLocations) {
    final List<LocationDTO> dtoList = new ArrayList<LocationDTO>(allLocations.size());
    for (Location location : allLocations) {
      dtoList.add(toDTO(location));
    }

    return dtoList;
  }

  private DTOAssembler() {
    // Prevent instantiation
  }

}
