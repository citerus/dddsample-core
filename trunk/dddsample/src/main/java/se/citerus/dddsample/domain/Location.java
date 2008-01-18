package se.citerus.dddsample.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Location {
  /**
   * Special Location object that marks an unknown location.
   */
  public static final Location UNKNOWN = new Location("Unknown");

  @Id
  @GeneratedValue
  private Long id;

  private String unlocode;

  public Location(String unlocode) {
    this.unlocode = unlocode;
  }

  /**
   * @return United Nations Location Code for this location.
   */
  public String unlocode() {
    return unlocode;
  }

  /**
   * @param object to compare
   * @return True if unlocodes are equal.
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
    return this.unlocode.equals(other.unlocode);
  }

  /**
   * @return Hash code of unlocode.
   */
  @Override
  public int hashCode() {
    return unlocode.hashCode();
  }

  @Override
  public String toString() {
    return unlocode;
  }

  // Needed by Hibernate
  Location() {}

}
