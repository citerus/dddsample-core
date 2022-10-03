package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;

public class HandlingActivityDTO {
    @Enumerated(value = EnumType.STRING)
    @Column(name = "next_expected_handling_event_type")
    public HandlingEventDTO.Type type;

    @ManyToOne
    @JoinColumn(name = "next_expected_location_fk", referencedColumnName = "next_expected_location_id")
    public LocationDTO location;

    @ManyToOne
    @JoinColumn(name = "next_expected_voyage_fk", referencedColumnName = "next_expected_voyage_id")
    public VoyageDTO voyage;
}
