package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CarrierMovementDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.VoyageDTO;

import java.util.stream.Collectors;

public class VoyageDTOConverter {

    public static VoyageDTO toDto(Voyage source) {
        VoyageDTO voyageDTO = new VoyageDTO();
        voyageDTO.voyageNumber = source.voyageNumber().idString();
        voyageDTO.carrierMovements = source.schedule().carrierMovements().stream().map(cm -> {
            CarrierMovementDTO dto = new CarrierMovementDTO();
            dto.arrivalLocation = new LocationDTO(
                    cm.arrivalLocation().unLocode().idString(),
                    cm.arrivalLocation().name());
            dto.departureLocation = new LocationDTO(
                    cm.departureLocation().unLocode().idString(),
                    cm.departureLocation().name());
            dto.arrivalTime = cm.arrivalTime();
            dto.departureTime = cm.departureTime();
            return dto;
        }).collect(Collectors.toList());
        return voyageDTO;
    }

    public static Voyage fromDto(VoyageDTO source) {
        return null;
    }
}
