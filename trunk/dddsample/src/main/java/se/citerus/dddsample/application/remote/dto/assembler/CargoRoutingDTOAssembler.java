package se.citerus.dddsample.application.remote.dto.assembler;

import se.citerus.dddsample.application.remote.dto.CargoRoutingDTO;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Leg;

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
