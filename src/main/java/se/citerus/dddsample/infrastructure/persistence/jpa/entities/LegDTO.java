package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "Leg")
@Table(name = "Leg")
public class LegDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @ManyToOne
    @JoinColumn(name="voyage_id")
    public VoyageDTO voyage;

    @ManyToOne
    @JoinColumn(name = "load_location_id")
    public LocationDTO loadLocation;

    @Column(name = "load_time")
    public Date loadTime;

    @ManyToOne
    @JoinColumn(name = "unload_location_id")
    public LocationDTO unloadLocation;

    @Column(name = "unload_time")
    public Date unloadTime;

    public LegDTO() {
    }

    public LegDTO(VoyageDTO voyage, LocationDTO loadLocation, Date loadTime, LocationDTO unloadLocation, Date unloadTime) {
        this.voyage = voyage;
        this.loadLocation = loadLocation;
        this.loadTime = loadTime;
        this.unloadLocation = unloadLocation;
        this.unloadTime = unloadTime;
    }

    public LegDTO(VoyageDTO voyage, LocationDTO loadLocation, LocationDTO unloadLocation, Date loadTime, Date unloadTime) {
        this.voyage = voyage;
        this.loadLocation = loadLocation;
        this.loadTime = loadTime;
        this.unloadLocation = unloadLocation;
        this.unloadTime = unloadTime;
    }
}
