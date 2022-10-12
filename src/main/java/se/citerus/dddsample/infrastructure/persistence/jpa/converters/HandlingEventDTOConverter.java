package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CargoDTO;
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
        return null; // TODO implement this
    }
}
