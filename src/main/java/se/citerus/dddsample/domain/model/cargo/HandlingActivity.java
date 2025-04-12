package se.citerus.dddsample.domain.model.cargo;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Objects;

/**
 * A handling activity represents how and where a cargo can be handled,
 * and can be used to express predictions about what is expected to
 * happen to a cargo in the future.
 *
 */
@Embeddable
public class HandlingActivity implements ValueObject<HandlingActivity> {

  // TODO make HandlingActivity a part of HandlingEvent too? There is some overlap. 

  @Enumerated(value = EnumType.STRING)
  @Column(name = "next_expected_handling_event_type")
  public HandlingEvent.Type type;

  @ManyToOne()
  @JoinColumn(name = "next_expected_location_id")
  public Location location;

  @ManyToOne
  @JoinColumn(name = "next_expected_voyage_id")
  public Voyage voyage;

  public HandlingActivity(final HandlingEvent.Type type, final Location location) {
    Objects.requireNonNull(type, "Handling event type is required");
    Objects.requireNonNull(location, "Location is required");

    this.type = type;
    this.location = location;
  }

  public HandlingActivity(final HandlingEvent.Type type, final Location location, final Voyage voyage) {
    Objects.requireNonNull(type, "Handling event type is required");
    Objects.requireNonNull(location, "Location is required");
    Objects.requireNonNull(location, "Voyage is required");

    this.type = type;
    this.location = location;
    this.voyage = voyage;
  }

  public HandlingEvent.Type type() {
    return type;
  }

  public Location location() {
    return location;
  }

  public Voyage voyage() {
    return voyage;
  }

  @Override
  public boolean sameValueAs(final HandlingActivity other) {
    return other != null && new EqualsBuilder().
      append(this.type, other.type).
      append(this.location, other.location).
      append(this.voyage, other.voyage).
      isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(this.type).
      append(this.location).
      append(this.voyage).
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

  protected HandlingActivity() {
    // Needed by Hibernate
  }

}
