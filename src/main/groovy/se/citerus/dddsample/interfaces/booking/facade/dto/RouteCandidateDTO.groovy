package se.citerus.dddsample.interfaces.booking.facade.dto

/**
 * DTO for presenting and selecting an itinerary from a collection of candidates.
 */
//@Immutable
final class RouteCandidateDTO implements Serializable {

  final List<LegDTO> legs

  RouteCandidateDTO(List<LegDTO> legs) {
    this.legs = legs
  }

}