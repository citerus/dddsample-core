package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Embeddable
public class RouteSpecificationDTO {
    @ManyToOne
    @JoinColumn(name = "spec_origin_id")
    public LocationDTO origin;

    @ManyToOne
    @JoinColumn(name = "spec_destination_id")
    public LocationDTO destination;

    @Column(name = "spec_arrival_deadline", nullable = false)
    public Date arrivalDeadline;

    public RouteSpecificationDTO() {
    }

    public RouteSpecificationDTO(LocationDTO origin, LocationDTO destination, Date arrivalDeadline) {
        this.origin = origin;
        this.destination = destination;
        this.arrivalDeadline = arrivalDeadline;
    }
}
