package se.citerus.dddsample.service.dto;

import java.util.Collections;
import java.util.List;

/**
 * DTO for presenting and selecting an itinerary from a collection of candidates.
 * 
 */
public class ItineraryCandidateDTO {
  List<LegDTO> legs;

  public ItineraryCandidateDTO(List<LegDTO> legs) {
    this.legs = legs;
  }

  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }
}
