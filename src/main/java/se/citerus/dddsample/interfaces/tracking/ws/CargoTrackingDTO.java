package se.citerus.dddsample.interfaces.tracking.ws;

import java.util.List;

/**
 * A data-transport object class representing a view of a Cargo entity.
 * Used by the REST API for public cargo tracking.
 */
public class CargoTrackingDTO {

    public final String trackingId;
    public final String statusText;
    public final String destination;
    public final String eta;
    public final String nextExpectedActivity;
    public final boolean isMisdirected;
    public final List<HandlingEventDTO> handlingEvents;

    public CargoTrackingDTO(String trackingId, String statusText, String destination, String eta, String nextExpectedActivity, boolean isMisdirected, List<HandlingEventDTO> handlingEvents) {
        this.trackingId = trackingId;
        this.statusText = statusText;
        this.destination = destination;
        this.eta = eta;
        this.nextExpectedActivity = nextExpectedActivity;
        this.isMisdirected = isMisdirected;
        this.handlingEvents = handlingEvents;
    }
}
