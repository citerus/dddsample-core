package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.Delivery;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.DeliveryDTO;

import java.util.List;
import java.util.stream.Collectors;

public class DeliveryDTOConverter {
    public static DeliveryDTO toDto(Delivery source) {
        return new DeliveryDTO(
                source.isMisdirected(),
                source.estimatedTimeOfArrival(),
                source.calculatedAt(),
                source.isUnloadedAtDestination(),
                source.routingStatus(),
                HandlingActivityDTOConverter.toDto(source.nextExpectedActivity()),
                source.transportStatus(),
                VoyageDTOConverter.toDto(source.currentVoyage()),
                LocationDTOConverter.toDto(source.lastKnownLocation()),
                null // TODO no setter exists for last event. Intentional?
        );
    }

    public static Delivery fromDto(DeliveryDTO source) {
        Voyage currentVoyage = VoyageDTOConverter.fromDto(source.currentVoyage);
        List<Leg> list = source.currentVoyage.carrierMovements.stream().map(cm -> LegDtoConverter.fromCarrierMovement(cm, currentVoyage)).collect(Collectors.toList());
        return new Delivery(
                HandlingEventDTOConverter.fromDto(source.lastEvent),
                new Itinerary(list),
                new RouteSpecification(list.get(0).loadLocation(), list.get(list.size()-1).unloadLocation(), list.get(list.size()-1).unloadTime())
        );
    }
}
