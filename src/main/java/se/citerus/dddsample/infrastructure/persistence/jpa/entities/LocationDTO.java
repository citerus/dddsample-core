package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;

@Entity(name = "Location")
@Table(name = "Location")
public class LocationDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false, unique = true)
    public String unlocode;

    @Column(nullable = false)
    public String name;
}
