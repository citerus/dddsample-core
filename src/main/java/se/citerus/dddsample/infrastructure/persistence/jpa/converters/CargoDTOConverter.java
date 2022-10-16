package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CargoDTO;

import java.util.stream.Collectors;

public class CargoDTOConverter {

    public static CargoDTO toDto(Cargo source) {
        CargoDTO cargoDTO = new CargoDTO(
                source.trackingId().idString(),
                LocationDTOConverter.toDto(source.origin()),
                RouteSpecificationDTOConverter.toDto(source.routeSpecification()),
                source.itinerary() != null ? source.itinerary().legs().stream().map(LegDtoConverter::toDto).collect(Collectors.toList()) : null,
                DeliveryDTOConverter.toDto(source.delivery(), source.routeSpecification())
        );
        return cargoDTO;
    }

    public static Cargo fromDto(CargoDTO source) {
        return new Cargo(
                new TrackingId(source.trackingId),
                RouteSpecificationDTOConverter.fromDto(source.routeSpecification),
                new Itinerary(source.itinerary.stream().map(LegDtoConverter::fromDto).collect(Collectors.toList())));
    }
}
