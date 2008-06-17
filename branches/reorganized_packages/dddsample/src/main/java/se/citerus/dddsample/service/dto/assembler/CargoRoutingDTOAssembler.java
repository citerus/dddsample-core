package se.citerus.dddsample.service.dto.assembler;

import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.Leg;
import se.citerus.dddsample.service.dto.CargoRoutingDTO;

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
