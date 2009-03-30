package se.citerus.dddsample.interfaces.booking.facade.internal

import se.citerus.dddsample.domain.model.cargo.Itinerary
import se.citerus.dddsample.domain.model.cargo.Leg
import se.citerus.dddsample.domain.model.location.UnLocode
import se.citerus.dddsample.domain.model.voyage.VoyageNumber
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO
import se.citerus.dddsample.domain.model.cargo.Cargo
import se.citerus.dddsample.domain.model.cargo.RoutingStatus
import se.citerus.dddsample.domain.model.location.Location
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO

class DTOAssembly {

  static void applyMixins() {
    applyToDTOMixins()
    applyFromDTOMixins()
  }

  private static def applyToDTOMixins() {
    applyLegToDTOMixin()
    applyCargoToDTOMixin()
    applyItineraryToDTOMixin()
    applyLocationToDTOMixin()
  }

  private static def applyFromDTOMixins() {
    applyLegFromDTOMixin()
    applyItineraryFromDTOMixin()
  }

  private static def applyItineraryToDTOMixin() {
    Itinerary.metaClass.toDTO {->
      new RouteCandidateDTO(legs().collect { it.toDTO() })
    }
  }

  private static def applyCargoToDTOMixin() {
    Cargo.metaClass.toDTO {->
      new CargoRoutingDTO(
        trackingId().idString(),
        origin().unLocode().idString(),
        routeSpecification().destination().unLocode().idString(),
        routeSpecification().arrivalDeadline(),
        delivery().routingStatus().sameValueAs(RoutingStatus.MISROUTED),
        itinerary().legs().collect { it.toDTO() }
      )
    }
  }

  private static def applyLegToDTOMixin() {
    Leg.metaClass.toDTO {->
      new LegDTO(
        voyage().voyageNumber().idString(),
        loadLocation().unLocode().idString(),
        unloadLocation().unLocode().idString(),
        loadTime(),
        unloadTime()
      )
    }
  }

  private static def applyLocationToDTOMixin() {
    Location.metaClass.toDTO {->
      new LocationDTO(unLocode().idString(),name())
    }
  }

  private static def applyItineraryFromDTOMixin() {
    Itinerary.metaClass.'static'.fromDTO = {dto, voyageRepository, locationRepository ->
      new Itinerary(dto.legs.collect {
        Leg.fromDTO(it, voyageRepository, locationRepository)
      })
    }
  }

  private static def applyLegFromDTOMixin() {
    Leg.metaClass.'static'.fromDTO = {dto, voyageRepository, locationRepository ->
      def voyageNumber = new VoyageNumber(dto.voyageNumber)
      def voyage = voyageRepository.find(voyageNumber)
      def loadLocation = locationRepository.find(new UnLocode(dto.from))
      def unloadLocation = locationRepository.find(new UnLocode(dto.to))

      new Leg(voyage, loadLocation, unloadLocation, dto.loadTime, dto.unloadTime)
    }
  }
}