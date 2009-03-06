package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.ValueObject;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;

/**
 * A handling activity represents how and where a cargo can be handled,
 * and can be used to express predictions about what is expected to
 * happen to a cargo in the future.
 *
 */
public class HandlingActivity implements ValueObject<HandlingActivity> {

  private HandlingEvent.Type type;
  private Location location;
  public static final HandlingActivity NONE = createNoneInstance();

  HandlingActivity() {
  }

  private static HandlingActivity createNoneInstance() {
    HandlingActivity none = new HandlingActivity();
    none.location = Location.UNKNOWN;
    return none;
  }

  public HandlingActivity(final HandlingEvent.Type type, final Location location) {
    Validate.notNull(type, "Handling event type is required");
    Validate.notNull(location, "Location is required");

    this.type = type;
    this.location = location;
  }


  @Override
  public boolean sameValueAs(final HandlingActivity other) {
    return other != null && new EqualsBuilder().
      append(this.type, other.type).
      append(this.location, other.location).
      isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(this.type).
      append(this.location).
      toHashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) return true;
    if (obj == null) return false;
    if (obj.getClass() != this.getClass()) return false;

    HandlingActivity other = (HandlingActivity) obj;

    return sameValueAs(other);
  }

  @Override
  public String toString() {
    if (this == NONE) return "No activity";

    return type + " in " + location;
  }

}
