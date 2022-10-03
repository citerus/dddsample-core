package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "HandlingEvent")
@Table(name = "HandlingEvent")
public class HandlingEventDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voyage_id")
    public VoyageDTO voyage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    public LocationDTO location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id")
    public CargoDTO cargo;

    @Column
    public Date completionTime;

    @Column
    public Date registrationTime;

    @Column
    @Enumerated(value = EnumType.STRING)
    public Type type;

    public enum Type {
        LOAD,
        UNLOAD,
        RECEIVE,
        CLAIM,
        CUSTOMS,
    }
}
