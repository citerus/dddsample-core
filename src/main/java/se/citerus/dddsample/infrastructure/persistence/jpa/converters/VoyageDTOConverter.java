package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.CarrierMovement;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CarrierMovementDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.VoyageDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VoyageDTOConverter {

    public static VoyageDTO toDto(Voyage source) {
        if (source == null) {
            return null;
        }
        String voyageNumber = source.voyageNumber().idString();
        List<CarrierMovementDTO> carrierMovementDTOS = source.schedule()
                .carrierMovements()
                .stream()
                .map(VoyageDTOConverter::convertToDto)
                .collect(Collectors.toList());
        return new VoyageDTO(voyageNumber, carrierMovementDTOS);
    }

    public static Voyage fromDto(VoyageDTO source) {
        List<CarrierMovementDTO> carrierMovements = new ArrayList<>(source.carrierMovements);
        Location departureLocation = LocationDTOConverter.fromDto(carrierMovements.get(0).departureLocation);
        Voyage.Builder builder = new Voyage.Builder(new VoyageNumber(source.voyageNumber), departureLocation);
        for (CarrierMovementDTO dto: carrierMovements) {
            builder.addMovement(
                    LocationDTOConverter.fromDto(dto.arrivalLocation),
                    dto.departureTime,
                    dto.arrivalTime
            );
        }
        return builder.build();
    }

    public static CarrierMovementDTO convertToDto(CarrierMovement cm) {
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
    }
}
