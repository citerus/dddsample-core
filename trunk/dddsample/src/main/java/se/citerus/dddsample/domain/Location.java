package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "locations")
public class Location {
  /**
   * The NULL Location object
   */
  public static final Location NULL = new Location("Unknown");

  @Id
  private Long id;

  @Column(name = "unlocode")
  private String unlocode;

  // Exclude the id field from equals() and hashcode()
  private static final String[] excludedFields = {"id"};

  // Needed by Hibernate
  Location() {}

  public Location(String unlocode) {
    this.unlocode = unlocode;
  }

  public String unlocode() {
    return unlocode;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == NULL || obj == NULL) {
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

}
