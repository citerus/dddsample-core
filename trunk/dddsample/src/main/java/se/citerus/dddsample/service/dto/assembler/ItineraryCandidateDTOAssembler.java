package se.citerus.dddsample.service.dto.assembler;

import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.Leg;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * Assembler class for the ItineraryCandidateDTO.
 */
public class ItineraryCandidateDTOAssembler {

  public ItineraryCandidateDTO toDTO(final Itinerary itinerary) {
    final List<LegDTO> legDTOs = new ArrayList<LegDTO>(itinerary.legs().size());
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

}
