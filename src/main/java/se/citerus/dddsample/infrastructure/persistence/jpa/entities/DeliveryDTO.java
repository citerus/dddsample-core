package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import se.citerus.dddsample.domain.model.cargo.RoutingStatus;
import se.citerus.dddsample.domain.model.cargo.TransportStatus;

import javax.persistence.*;
import java.util.Date;

@Embeddable
public class DeliveryDTO {
    @Column
    public boolean misdirected;

    @Column
    public Date eta;

    @Column(name = "calculated_at")
    public Date calculatedAt;

    @Column(name = "unloaded_at_dest")
    public boolean isUnloadedAtDestination;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "routing_status")
    public RoutingStatus routingStatus;

    @Embedded
    public HandlingActivityDTO nextExpectedActivity;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "transport_status")
    public TransportStatus transportStatus;

    @ManyToOne
    @JoinColumn(name = "current_voyage_id")
    public VoyageDTO currentVoyage;

    @ManyToOne
    @JoinColumn(name = "last_known_location_id")
    public LocationDTO lastKnownLocation;

    @ManyToOne
    @JoinColumn(name = "last_event_id")
    public HandlingEventDTO lastEvent;

    public DeliveryDTO() {
    }

    public DeliveryDTO(boolean misdirected, Date eta, Date calculatedAt, boolean isUnloadedAtDestination, RoutingStatus routingStatus, HandlingActivityDTO nextExpectedActivity, TransportStatus transportStatus, VoyageDTO currentVoyage, LocationDTO lastKnownLocation, HandlingEventDTO lastEvent) {
        this.misdirected = misdirected;
        this.eta = eta;
        this.calculatedAt = calculatedAt;
        this.isUnloadedAtDestination = isUnloadedAtDestination;
        this.routingStatus = routingStatus;
        this.nextExpectedActivity = nextExpectedActivity;
        this.transportStatus = transportStatus;
        this.currentVoyage = currentVoyage;
        this.lastKnownLocation = lastKnownLocation;
        this.lastEvent = lastEvent;
    }
}
