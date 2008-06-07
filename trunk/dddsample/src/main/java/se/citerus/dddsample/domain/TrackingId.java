package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Identifies a particular cargo.
 * <p/>
 * Make sure to put a constraint in the database to make sure TrackingId is unique.
 */
public final class TrackingId {

  private String id;

  /**
   * Constructor.
   *
   * @param id Id string.
   */
  public TrackingId(final String id) {
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
  public boolean equals(final Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }


  TrackingId() {
    // Needed by Hibernate
  }

}
