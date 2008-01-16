package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

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

  // Exclude the id field from equals() and hashcode()
  private static final String[] excludedFields = {"id"};

  public Location(String unlocode) {
    this.unlocode = unlocode;
  }

  public String unlocode() {
    return unlocode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == UNKNOWN || obj == UNKNOWN) {
      return this == obj;
    }
    return EqualsBuilder.reflectionEquals(this, obj, excludedFields);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this, excludedFields);
  }

  @Override
  public String toString() {
    return unlocode;
  }

  // Needed by Hibernate
  Location() {}

}
