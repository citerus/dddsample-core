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

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "voyage_id")
    public Collection<CarrierMovementDTO> carrierMovements;

    public VoyageDTO() {
    }

    public VoyageDTO(String voyageNumber, Collection<CarrierMovementDTO> carrierMovements) {
        this.voyageNumber = voyageNumber;
        this.carrierMovements = carrierMovements;
    }
}
