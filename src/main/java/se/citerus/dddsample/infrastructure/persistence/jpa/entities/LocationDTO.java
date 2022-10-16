package se.citerus.dddsample.infrastructure.persistence.jpa.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity(name = "Location")
@Table(name = "Location")
public class LocationDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long id;

    @Column(nullable = false, unique = true, updatable = false)
    public String unlocode;

    @Column(nullable = false)
    public String name;

    public LocationDTO() {
    }

    public LocationDTO(String unlocode, String name) {
        this.unlocode = unlocode;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationDTO that = (LocationDTO) o;
        return unlocode.equals(that.unlocode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(unlocode);
    }
}
