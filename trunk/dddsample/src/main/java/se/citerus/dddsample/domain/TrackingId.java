package se.citerus.dddsample.domain;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * TrackingId is a simple ID wrapper which implements Serializable for easier
 * integration with persistence frameworks.
 * 
 */
@Embeddable
public class TrackingId implements Serializable {

  private static final long serialVersionUID = 6273117599327914522L;

  private String id;

  public TrackingId(String id) {
    this.id = id;
  }

  public String idString() {
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

  // Needed by Hibernate
  TrackingId() {}

}
