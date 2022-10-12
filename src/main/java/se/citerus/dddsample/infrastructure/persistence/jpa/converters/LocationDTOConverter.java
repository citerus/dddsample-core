package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;

public class LocationDTOConverter {
    public static LocationDTO toDto(Location source) {
        return new LocationDTO(source.unLocode().idString(), source.name());
    }

    public static Location fromDto(LocationDTO source) {
        return null; // TODO implement this
    }
}
