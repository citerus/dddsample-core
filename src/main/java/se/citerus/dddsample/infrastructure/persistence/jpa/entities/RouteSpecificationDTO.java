package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

public class RouteSpecificationDTO {
    @ManyToOne
    @JoinColumn(name = "spec_origin_fk", referencedColumnName = "origin_id")
    public LocationDTO origin;

    @ManyToOne
    @JoinColumn(name = "spec_destination_fk", referencedColumnName = "destination_id")
    public LocationDTO destination;

    @Column(name = "spec_arrival_deadline", nullable = false)
    public Date arrivalDeadline;
}
