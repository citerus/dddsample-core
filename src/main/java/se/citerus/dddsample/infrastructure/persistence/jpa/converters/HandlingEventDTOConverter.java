package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.HandlingActivity;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CargoDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingActivityDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingEventDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.VoyageDTO;

public class HandlingEventDTOConverter {
    public static HandlingEventDTO toDto(HandlingEvent source) {
        VoyageDTO voyage = VoyageDTOConverter.toDto(source.voyage());
        LocationDTO location = LocationDTOConverter.toDto(source.location());
        CargoDTO cargo = CargoDTOConverter.toDto(source.cargo());
        HandlingEventDTO.Type type = HandlingEventDTO.Type.valueOf(source.type().name());
        return new HandlingEventDTO(voyage, location, cargo, source.completionTime(), source.registrationTime(), type);
    }

    public static HandlingEvent fromDto(HandlingEventDTO source) {
        if (source.voyage != null && source.voyage.carrierMovements != null) {
            return new HandlingEvent(
                    CargoDTOConverter.fromDto(source.cargo),
                    source.completionTime,
                    source.registrationTime,
                    HandlingEvent.Type.valueOf(source.type.name()),
                    LocationDTOConverter.fromDto(source.location),
                    VoyageDTOConverter.fromDto(source.voyage));
        }
        return new HandlingEvent(
                CargoDTOConverter.fromDto(source.cargo),
                source.completionTime,
                source.registrationTime,
                HandlingEvent.Type.valueOf(source.type.name()),
                LocationDTOConverter.fromDto(source.location));
    }
}
