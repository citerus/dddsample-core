package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.RouteSpecificationDTO;

public class RouteSpecificationDTOConverter {
    public static RouteSpecificationDTO toDto(RouteSpecification source) {
        return new RouteSpecificationDTO(
                LocationDTOConverter.toDto(source.origin()),
                LocationDTOConverter.toDto(source.destination()),
                source.arrivalDeadline());
    }

    public static RouteSpecification fromDto(RouteSpecificationDTO source) {
        return new RouteSpecification(
                LocationDTOConverter.fromDto(source.origin),
                LocationDTOConverter.fromDto(source.destination),
                source.arrivalDeadline);
    }
}
