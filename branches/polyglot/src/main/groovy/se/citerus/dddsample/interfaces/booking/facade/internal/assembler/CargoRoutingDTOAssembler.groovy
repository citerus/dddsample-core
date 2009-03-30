package se.citerus.dddsample.interfaces.booking.facade.internal.assembler

import se.citerus.dddsample.domain.model.cargo.Cargo
import se.citerus.dddsample.domain.model.cargo.RoutingStatus
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO
import se.citerus.dddsample.domain.model.cargo.Leg

/**
 * Assembler class for the CargoRoutingDTO.
 */
class CargoRoutingDTOAssembler {

  /*
  def applyMixins() {
    Leg.metaClass.toDTO { ->
      new LegDTO(
          voyageNumber:voyage().voyageNumber().idString(),
          from:loadLocation().unLocode().idString(),
          to:unloadLocation().unLocode().idString(),
          loadTime:loadTime(),
          unloadTime:unloadTime()
      )
    }

    Cargo.metaClass.toDTO { ->
      new CargoRoutingDTO(
        trackingId: trackingId().idString(),
        origin: origin().unLocode().idString(),
        finalDestination: routeSpecification().destination().unLocode().idString(),
        arrivalDeadline: routeSpecification().arrivalDeadline(),
        misrouted: delivery().routingStatus().sameValueAs(RoutingStatus.MISROUTED),
        legs: itinerary().legs().collect { it.toDTO() }
      )
    }
  }
  */

  /**
   *
   * @param cargo cargo
   * @return A cargo routing DTO
   */
  CargoRoutingDTO toDTO(Cargo cargo) {
    new CargoRoutingDTO(
      cargo.trackingId().idString(),
      cargo.origin().unLocode().idString(),
      cargo.routeSpecification().destination().unLocode().idString(),
      cargo.routeSpecification().arrivalDeadline(),
      cargo.delivery().routingStatus().sameValueAs(RoutingStatus.MISROUTED),
      cargo.itinerary().legs().collect { toDTO(it) }
    )
  }

  LegDTO toDTO(Leg leg) {
    new LegDTO(
        leg.voyage().voyageNumber().idString(),
        leg.loadLocation().unLocode().idString(),
        leg.unloadLocation().unLocode().idString(),
        leg.loadTime(),
        leg.unloadTime()
    )
  }

}
