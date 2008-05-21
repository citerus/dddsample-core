package se.citerus.dddsample.service.dto;

import se.citerus.dddsample.domain.Itinerary;
import se.citerus.dddsample.domain.Leg;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles assembly of DTOs from the domain model.
 */
public class DTOAssembler {

  public static ItineraryCandidateDTO toItineraryCandidateDTO(Itinerary itinerary) {
    List<LegDTO> legDTOs = new ArrayList<LegDTO>(itinerary.legs().size());
    for (Leg leg : itinerary.legs()) {
      legDTOs.add(toLegDTO(leg));
    }
    return new ItineraryCandidateDTO(legDTOs);
  }

  private static LegDTO toLegDTO(Leg leg) {
    return new LegDTO(
      leg.carrierMovement().carrierMovementId().idString(),
      leg.from().unLocode().idString(),
      leg.to().unLocode().idString()
    );
  }
}
