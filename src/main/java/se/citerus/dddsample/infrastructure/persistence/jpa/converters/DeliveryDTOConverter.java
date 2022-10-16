package se.citerus.dddsample.infrastructure.persistence.jpa.converters;

import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.DeliveryDTO;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.HandlingActivityDTO;

import java.util.List;
import java.util.stream.Collectors;

public class DeliveryDTOConverter {
    public static DeliveryDTO toDto(Delivery source, RouteSpecification routeSpecification) {
        HandlingActivityDTO nextExpectedActivity = source.nextExpectedActivity() != null
                ? HandlingActivityDTOConverter.toDto(source.nextExpectedActivity())
                : HandlingActivityDTOConverter.toDto(new HandlingActivity(HandlingEvent.Type.RECEIVE, routeSpecification.origin()));
        return new DeliveryDTO(
                source.isMisdirected(),
                source.estimatedTimeOfArrival(),
                source.calculatedAt(),
                source.isUnloadedAtDestination(),
                source.routingStatus(),
                nextExpectedActivity,
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
