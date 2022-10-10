package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import se.citerus.dddsample.domain.model.cargo.Leg;

import javax.persistence.*;
import java.util.Collection;

@Entity(name = "Cargo")
@Table(name = "Cargo")
public class CargoDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(name = "tracking_id", unique = true)
    public String trackingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id")
    public LocationDTO origin;

    @Embedded
    public RouteSpecificationDTO routeSpecification;

    @OneToMany
    @JoinColumn(name = "cargo_id")
    public Collection<LegDTO> itinerary;

    @Embedded
    public DeliveryDTO delivery;

    public CargoDTO() {
    }

    public CargoDTO(String trackingId, LocationDTO origin, RouteSpecificationDTO routeSpecification, Collection<LegDTO> itinerary, DeliveryDTO delivery) {
        this.trackingId = trackingId;
        this.origin = origin;
        this.routeSpecification = routeSpecification;
        this.itinerary = itinerary;
        this.delivery = delivery;
    }
}
