package se.citerus.dddsample.interfaces.tracking.ws;

import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;

import java.util.List;

/**
 * A data-transport object class representing a view of a Cargo entity.
 * Used by the REST API for public cargo tracking.
 */
public class CargoTrackingDTO {

    public final String trackingId;
    public final String statusText;
    public final LocationDTO origin;
    public final LocationDTO destination;
    public final String eta;
    public final String nextExpectedActivity;
    public final boolean isMisdirected;
    public final List<CargoLegDTO> itinerary;
    public final List<HandlingEventDTO> handlingEvents;

    public CargoTrackingDTO(String trackingId, String statusText, LocationDTO origin, LocationDTO destination, String eta, String nextExpectedActivity, boolean isMisdirected, List<CargoLegDTO> legDTOS, List<HandlingEventDTO> handlingEvents) {
        this.trackingId = trackingId;
        this.statusText = statusText;
        this.origin = origin;
        this.destination = destination;
        this.eta = eta;
        this.nextExpectedActivity = nextExpectedActivity;
        this.isMisdirected = isMisdirected;
        this.itinerary = legDTOS;
        this.handlingEvents = handlingEvents;
    }
}
