package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class Location {
  /**
   * The NULL Location object
   */
  public static final Location NULL = new Location("");
  
  private final String unlocode;

  public Location(String unlocode) {
    this.unlocode = unlocode;
  }

  public String unlocode() {
    return unlocode;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return unlocode;
  }

}
