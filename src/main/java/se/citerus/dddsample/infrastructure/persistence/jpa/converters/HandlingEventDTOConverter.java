package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import org.springframework.core.convert.converter.Converter;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingEventDTO;

public class HandlingEventDTOConverter {
    public static HandlingEventDTO toDto(HandlingEvent source) {
        return new HandlingEventDTO();
    }

    public static HandlingEvent fromDto(HandlingEventDTO source) {
        return null;
    }
}
