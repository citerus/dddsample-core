package se.citerus.dddsample.tracking.core.interfaces.booking.facade.internal.assembler;

import se.citerus.dddsample.tracking.booking.api.dto.CargoRoutingDTO;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Leg;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RoutingStatus;

/**
 * Assembler class for the CargoRoutingDTO.
 */
public class CargoRoutingDTOAssembler {

  /**
   * @param cargo cargo
   * @return A cargo routing DTO
   */
  public CargoRoutingDTO toDTO(final Cargo cargo) {
    final CargoRoutingDTO dto = new CargoRoutingDTO(
      cargo.trackingId().stringValue(),
      cargo.routeSpecification().origin().unLocode().stringValue(),
      cargo.routeSpecification().destination().unLocode().stringValue(),
      cargo.routeSpecification().arrivalDeadline(),
      cargo.routingStatus().sameValueAs(RoutingStatus.MISROUTED));
    for (Leg leg : cargo.itinerary().legs()) {
      dto.addLeg(
        leg.voyage().voyageNumber().stringValue(),
        leg.loadLocation().unLocode().stringValue(),
        leg.unloadLocation().unLocode().stringValue(),
        leg.loadTime(),
        leg.unloadTime());
    }
    return dto;
  }

}
