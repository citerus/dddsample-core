package se.citerus.dddsample.application.remoting.dto.assembler;

import se.citerus.dddsample.application.remoting.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.application.remoting.dto.LegDTO;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler class for the ItineraryCandidateDTO.
 */
public class ItineraryCandidateDTOAssembler {
  
  public ItineraryCandidateDTO toDTO(final Itinerary itinerary) {
    final List<LegDTO> legDTOs = new ArrayList<se.citerus.dddsample.application.remoting.dto.LegDTO>(itinerary.legs().size());
    for (Leg leg : itinerary.legs()) {
      legDTOs.add(toLegDTO(leg));
    }
    return new ItineraryCandidateDTO(legDTOs);
  }

  protected LegDTO toLegDTO(final Leg leg) {
    final CarrierMovementId carrierMovementId = leg.carrierMovement().carrierMovementId();
    final UnLocode from = leg.from().unLocode();
    final UnLocode to = leg.to().unLocode();
    return new LegDTO(carrierMovementId.idString(), from.idString(), to.idString());
  }

  public Itinerary fromDTO(ItineraryCandidateDTO itineraryCandidateDTO, CarrierMovementRepository carrierMovementRepository, LocationRepository locationRepository) {
    final List<Leg> legs = new ArrayList<Leg>(itineraryCandidateDTO.getLegs().size());
    for (LegDTO legDTO : itineraryCandidateDTO.getLegs()) {
      final CarrierMovementId carrierMovementId = new CarrierMovementId(legDTO.getCarrierMovementId());
      final CarrierMovement carrierMovement = carrierMovementRepository.find(carrierMovementId);
      final Location from = locationRepository.find(new UnLocode(legDTO.getFrom()));
      final Location to = locationRepository.find(new UnLocode(legDTO.getTo()));
      legs.add(new Leg(carrierMovement, from, to));
    }
    return new Itinerary(legs);
  }
}
