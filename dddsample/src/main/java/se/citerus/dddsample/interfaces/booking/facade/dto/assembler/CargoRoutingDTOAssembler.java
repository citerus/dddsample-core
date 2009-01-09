package se.citerus.dddsample.interfaces.booking.facade.dto.assembler;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;

/**
 * Assembler class for the CargoRoutingDTO.
 */
public class CargoRoutingDTOAssembler {

  public CargoRoutingDTO toDTO(final Cargo cargo) {
    final CargoRoutingDTO dto = new CargoRoutingDTO(
      cargo.trackingId().idString(),
      cargo.origin().unLocode().idString(),
      cargo.routeSpecification().destination().unLocode().idString()
    );
    for (Leg leg : cargo.itinerary().legs()) {
      dto.addLeg(
        leg.voyage().voyageNumber().idString(),
        leg.loadLocation().unLocode().idString(),
        leg.unloadLocation().unLocode().idString()
      );
    }
    return dto;
  }

}
