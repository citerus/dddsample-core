package se.citerus.dddsample.application.service.assembler;

import se.citerus.dddsample.domain.model.Cargo;
import se.citerus.dddsample.domain.model.Leg;
import se.citerus.dddsample.application.service.api.dto.CargoRoutingDTO;

/**
 * Assembler class for the CargoRoutingDTO.
 */
public class CargoRoutingDTOAssembler {

  public CargoRoutingDTO toDTO(final Cargo cargo) {
    final CargoRoutingDTO dto = new CargoRoutingDTO(
      cargo.trackingId().idString(),
      cargo.origin().toString(),
      cargo.destination().toString()
    );
    for (Leg leg : cargo.itinerary().legs()) {
      dto.addLeg(
        leg.carrierMovement().carrierMovementId().idString(),
        leg.from().toString(),
        leg.to().toString()
      );
    }
    return dto;
  }

}
