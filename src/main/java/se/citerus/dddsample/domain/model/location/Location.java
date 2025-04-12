package se.citerus.dddsample.domain.model.location;

import jakarta.persistence.*;
import se.citerus.dddsample.domain.shared.DomainEntity;

import java.util.Objects;

/**
 * A location is our model is stops on a journey, such as cargo
 * origin or destination, or carrier movement endpoints.
 * It is uniquely identified by a UN Locode.
 *
 */
@Entity(name = "Location")
@Table(name = "Location")
public final class Location implements DomainEntity<Location> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false, unique = true, updatable = false)
  private String unlocode;

  @Column(nullable = false)
  private String name;

  /**
   * Special Location object that marks an unknown location.
   */
  public static final Location UNKNOWN = new Location(
    new UnLocode("XXXXX"), "Unknown location"
  );

  /**
   * Package-level constructor, visible for test and sample data purposes.
   *
   * @param unLocode UN Locode
   * @param name     location name
   * @throws IllegalArgumentException if the UN Locode or name is null
   */
  public Location(final UnLocode unLocode, final String name) {
    Objects.requireNonNull(unLocode);
    Objects.requireNonNull(name);
    
    this.unlocode = unLocode.idString();
    this.name = name;
  }

  // Used by JPA
  public Location(String unloCode, String name) {
    this.unlocode = unloCode;
    this.name = name;
  }

  /**
   * @return UN Locode for this location.
   */
  public UnLocode unLocode() {
    return new UnLocode(unlocode);
  }

  /**
   * @return Actual name of this location, e.g. "Stockholm".
   */
  public String name() {
    return name;
  }

  public String code() {
    return unlocode;
  }

  public long id() {
    return id;
  }

  /**
   * @param object to compare
   * @return Since this is an entiy this will be true iff UN locodes are equal.
   */
  @Override
  public boolean equals(final Object object) {
    if (object == null) {
      return false;
    }
    if (this == object) {
      return true;
    }
    if (!(object instanceof Location other)) {
      return false;
    }
      return sameIdentityAs(other);
  }

  @Override
  public boolean sameIdentityAs(final Location other) {
    return this.unlocode.equals(other.unlocode);
  }

  /**
   * @return Hash code of UN locode.
   */
  @Override
  public int hashCode() {
    return unlocode.hashCode();
  }

  @Override
  public String toString() {
    return name + " [" + unlocode + "]";
  }

  Location() {
    // Needed by Hibernate
  }

}
