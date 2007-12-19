package se.citerus.dddsample.domain;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * TrackingId is a simple ID wrapper which implements Serializable for easier
 * integration with persistens frameworks.
 * 
 */
public class TrackingId implements Serializable {

  private static final long serialVersionUID = 6273117599327914522L;
  private final String id;

  public TrackingId(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }

}
