package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.HandlingActivity;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingActivityDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingEventDTO;

public class HandlingActivityDTOConverter {
    public static HandlingActivityDTO toDto(HandlingActivity source) {
        return new HandlingActivityDTO(
                HandlingEventDTO.Type.valueOf(source.type().name()),
                LocationDTOConverter.toDto(source.location()),
                VoyageDTOConverter.toDto(source.voyage())
        );
    }

    public static HandlingActivity fromDto(HandlingActivityDTO source) {
        if (source.voyage != null) {
            return new HandlingActivity(
                    HandlingEvent.Type.valueOf(source.type.name()),
                    LocationDTOConverter.fromDto(source.location),
                    VoyageDTOConverter.fromDto(source.voyage)
            );
        }
        return new HandlingActivity(
                HandlingEvent.Type.valueOf(source.type.name()),
                LocationDTOConverter.fromDto(source.location)
        );
    }
}
