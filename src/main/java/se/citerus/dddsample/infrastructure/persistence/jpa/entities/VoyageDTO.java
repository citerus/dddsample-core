package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Collection;

@Entity(name = "Voyage")
@Table(name = "Voyage")
public class VoyageDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(name = "voyage_number", unique = true)
    public String voyageNumber;

    @OneToMany
    @JoinColumn(name = "voyage_fk", referencedColumnName = "voyage_id")
    public Collection<CarrierMovementDTO> carrierMovements;
}
