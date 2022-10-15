package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CarrierMovementDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LegDTO;

public class LegDtoConverter {
    public static LegDTO toDto(Leg source) {
        return new LegDTO(VoyageDTOConverter.toDto(source.voyage()),
                LocationDTOConverter.toDto(source.loadLocation()),
                source.loadTime(),
                LocationDTOConverter.toDto(source.unloadLocation()),
                source.unloadTime());
    }

    public static Leg fromDto(LegDTO source) {
        return new Leg(
                VoyageDTOConverter.fromDto(source.voyage),
                LocationDTOConverter.fromDto(source.loadLocation),
                LocationDTOConverter.fromDto(source.unloadLocation),
                source.loadTime,
                source.unloadTime
        );
    }

    public static Leg fromCarrierMovement(CarrierMovementDTO carrierMovementDTO, Voyage voyage) {
        return new Leg(voyage,
                LocationDTOConverter.fromDto(carrierMovementDTO.departureLocation),
                LocationDTOConverter.fromDto(carrierMovementDTO.arrivalLocation),
                carrierMovementDTO.departureTime,
                carrierMovementDTO.arrivalTime);
    }
}
