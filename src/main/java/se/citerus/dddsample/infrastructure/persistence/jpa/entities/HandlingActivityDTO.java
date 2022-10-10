package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;

@Embeddable
public class HandlingActivityDTO {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "next_expected_handling_event_type")
    public HandlingEventDTO.Type type;

    @ManyToOne
    @JoinColumn(name = "next_expected_location_id")
    public LocationDTO location;

    @ManyToOne
    @JoinColumn(name = "next_expected_voyage_id")
    public VoyageDTO voyage;

    public HandlingActivityDTO() {
    }

    public HandlingActivityDTO(HandlingEventDTO.Type type, LocationDTO location, VoyageDTO voyage) {
        this.type = type;
        this.location = location;
        this.voyage = voyage;
    }
}
