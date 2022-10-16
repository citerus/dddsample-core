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

    @ManyToOne(fetch = FetchType.EAGER)
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

    public HandlingEventDTO(VoyageDTO voyage, LocationDTO location, CargoDTO cargo, Date completionTime, Date registrationTime, Type type) {
        this.voyage = voyage;
        this.location = location;
        this.cargo = cargo;
        this.completionTime = completionTime;
        this.registrationTime = registrationTime;
        this.type = type;
    }

    /**
     * Constructor used for events before a voyage has been selected for the cargo. Registration time defaults to now.
     */
    public HandlingEventDTO(LocationDTO location, CargoDTO cargo, Date completionTime, Type type) {
        this(null, location, cargo, completionTime, new Date(), type);
    }

    /**
     * Constructor that defaults registration time to now.
     */
    public HandlingEventDTO(VoyageDTO voyage, LocationDTO location, CargoDTO cargo, Date completionTime, Type type) {
        this(voyage, location, cargo, completionTime, new Date(), type);
    }

    public HandlingEventDTO() {
    }
}
