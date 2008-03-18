package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Location {

  @Id
  @GeneratedValue
  private Long id;

  @Embedded
  private UnLocode unLocode;

  private String name;

  /**
   * Special Location object that marks an unknown location.
   */
  public static final Location UNKNOWN = new Location(
     new UnLocode("XX", "XXX"), "Unknown location"
  );

  /**
   * @param unLocode UN Locode
   * @param name     location name
   * @throws IllegalArgumentException if the UN Locode or name is null
   */
  public Location(UnLocode unLocode, String name) {
    // TODO:
    // It shouldn't really be possible to create a new location -
    // it should only be looked up in the location repository.
    Validate.notNull(unLocode);
    Validate.notNull(name);
    this.unLocode = unLocode;
    this.name = name;
  }

  /**
   * @return UN Locode for this location.
   */
  public UnLocode unLocode() {
    return unLocode;
  }

  /**
   * @return Actual name of this location, e.g. "Stockholm".
   */
  public String name() {
    return name;
  }

  /**
   * @param object to compare
   * @return Since this is an entiy this will be true iff UN locodes are equal.
   */
  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    }
    if (this == UNKNOWN || object == UNKNOWN) {
      return this == object;
    }
    if (!(object instanceof Location)) {
      return false;
    }
    Location other = (Location) object;
    return sameIdentityAs(other);
  }

  /**
   * Entities compare by identity, therefore the unLocode field is the only basis of comparison. For persistence we
   * have an id field, but it is not used for identiy comparison.
   *
   * Compare this behavior to the value object {@link se.citerus.dddsample.domain.Leg#sameValueAs(Leg)}
   *
   * @param other The other location.
   * @return <code>true</code> if the given location's and this locations's unLocode is the same, regardles of other
   * attributes.
   */
  public boolean sameIdentityAs(Location other) {
    return this.unLocode.equals(other.unLocode);
  }

  /**
   * @return Hash code of UN locode.
   */
  @Override
  public int hashCode() {
    return unLocode.hashCode();
  }

  /**
   * @return Unlocode and name, on the format "SESTO (Stockholm)"
   */
  @Override
  public String toString() {
    return unLocode.idString() + " (" + name + ")";
  }

  // Needed by Hibernate
  Location() {
  }

}
