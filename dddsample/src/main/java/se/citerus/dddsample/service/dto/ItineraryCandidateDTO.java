package se.citerus.dddsample.service.dto;

import java.util.Collections;
import java.util.List;

/**
 * DTO for presenting and selecting an itinerary from a collection of candidates.
 */
public final class ItineraryCandidateDTO {

  private final List<LegDTO> legs;

  public ItineraryCandidateDTO(final List<LegDTO> legs) {
    this.legs = legs;
  }

  /**
   * @return An unmodifiable list DTOs.
   */
  public List<LegDTO> getLegs() {
    return Collections.unmodifiableList(legs);
  }
}
