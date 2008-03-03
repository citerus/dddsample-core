package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Identifies a particular cargo.
 *
 */
@Embeddable
public class TrackingId {

  @Column(name = "tracking_id")
  private String id;

  public TrackingId(String id) {
    Validate.notNull(id);
    this.id = id;
  }

  /**
   * @return String representation of this tracking id.
   */
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
