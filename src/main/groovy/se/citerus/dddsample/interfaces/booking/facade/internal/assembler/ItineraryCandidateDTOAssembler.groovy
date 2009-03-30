package se.citerus.dddsample.interfaces.booking.facade.internal.assembler

import se.citerus.dddsample.domain.model.cargo.Itinerary
import se.citerus.dddsample.domain.model.cargo.Leg
import se.citerus.dddsample.domain.model.location.Location
import se.citerus.dddsample.domain.model.location.LocationRepository
import se.citerus.dddsample.domain.model.location.UnLocode
import se.citerus.dddsample.domain.model.voyage.Voyage
import se.citerus.dddsample.domain.model.voyage.VoyageNumber
import se.citerus.dddsample.domain.model.voyage.VoyageRepository
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO

/**
 * Assembler class for the ItineraryCandidateDTO.
 */
class ItineraryCandidateDTOAssembler {

  /**
   * @param itinerary itinerary
   * @return A route candidate DTO
   */
  RouteCandidateDTO toDTO(Itinerary itinerary) {
    new RouteCandidateDTO(itinerary.legs().collect { toLegDTO(it) })
  }

  /**
   * @param leg leg
   * @return A leg DTO
   */
  protected LegDTO toLegDTO(Leg leg) {
    new LegDTO(
          leg.voyage().voyageNumber().idString(),
          leg.loadLocation().unLocode().idString(),
          leg.unloadLocation().unLocode().idString(),
          leg.loadTime(),
          leg.unloadTime()
    )
  }

  /**
   * @param routeCandidateDTO route candidate DTO
   * @param voyageRepository voyage repository
   * @param locationRepository location repository
   * @return An itinerary
   */
  Itinerary fromDTO(RouteCandidateDTO routeCandidateDTO,
                    VoyageRepository voyageRepository,
                    LocationRepository locationRepository) {

    def legs = routeCandidateDTO.legs.collect { dto ->
      def voyageNumber = new VoyageNumber(dto.voyageNumber)
      def voyage = voyageRepository.find(voyageNumber)
      def loadLocation = locationRepository.find(new UnLocode(dto.from))
      def unloadLocation = locationRepository.find(new UnLocode(dto.to))

      new Leg(voyage, loadLocation, unloadLocation, dto.loadTime, dto.unloadTime)
    }

    new Itinerary(legs)
  }
  
}
