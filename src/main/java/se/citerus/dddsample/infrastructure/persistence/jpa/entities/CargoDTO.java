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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_fk", referencedColumnName = "origin_id")
    public LocationDTO origin;

    @Column(name = "tracking_id", unique = true)
    public String trackingId;

    public DeliveryDTO delivery;

    public RouteSpecificationDTO routeSpecification;

    @OneToMany
    @JoinColumn(name = "itinerary_fk", referencedColumnName = "cargo_id")
    public Collection<Leg> itinerary;
}
