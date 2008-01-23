package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.regex.Pattern;

@Entity
public class Location {

  @Id
  @GeneratedValue
  private Long id;

  private String unlocode;

  /**
   * Regular expression of a UN Locode (exactly five letters of the english alphabet)
   */
  private static final Pattern unlocodePattern = Pattern.compile("[a-zA-Z]{5}");

  /**
   * Special Location object that marks an unknown location.
   */
  public static final Location UNKNOWN = new Location();

  // Internal constructor
  private Location() {
    this.unlocode = "Unknown";
  }

  /**
   * @param unlocode UN locode
   * @throws IllegalArgumentException if the UN locode is anything other than five letters of the US alphabet
   */
  public Location(String unlocode) {
    Validate.notNull(unlocode);
    Validate.isTrue(unlocodePattern.matcher(unlocode).matches(),
            "\"" + unlocode + "\" is not a valid UN Locode");
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

}
