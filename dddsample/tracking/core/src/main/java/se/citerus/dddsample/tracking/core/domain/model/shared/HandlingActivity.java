package se.citerus.dddsample.tracking.core.domain.model.shared;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

/**
 * A handling activity represents how and where a cargo can be handled,
 * and can be used to express predictions about what is expected to
 * happen to a cargo in the future.
 */
public class HandlingActivity extends ValueObjectSupport<HandlingActivity> {

  private final HandlingEvent.Type type;
  private final Location location;
  private final Voyage voyage;

  public HandlingActivity(final HandlingEvent.Type type, final Location location) {
    Validate.notNull(type, "Handling event type is required");
    Validate.notNull(location, "Location is required");

    this.type = type;
    this.location = location;
    this.voyage = null;
  }

  public HandlingActivity(final HandlingEvent.Type type, final Location location, final Voyage voyage) {
    Validate.notNull(type, "Handling event type is required");
    Validate.notNull(location, "Location is required");
    Validate.notNull(voyage, "Voyage is required");

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
  public String toString() {
    return type + " in " + location + (voyage != null ? ", " + voyage : "");
  }

  HandlingActivity() {
    // Needed by Hibernate
    type = null;
    location = null;
    voyage = null;
  }

  // DSL-like factory methods

  public static InLocation loadedOnto(Voyage voyage) {
    return new InLocation(LOAD, voyage);
  }

  public static InLocation unloadedOff(Voyage voyage) {
    return new InLocation(UNLOAD, voyage);
  }

  public static HandlingActivity receivedIn(Location location) {
    return new HandlingActivity(RECEIVE, location);
  }

  public static HandlingActivity claimedIn(Location location) {
    return new HandlingActivity(CLAIM, location);
  }

  public static HandlingActivity customsIn(Location location) {
    return new HandlingActivity(CUSTOMS, location);
  }

  public static class InLocation {
    private final HandlingEvent.Type type;
    private final Voyage voyage;

    public InLocation(HandlingEvent.Type type, Voyage voyage) {
      this.type = type;
      this.voyage = voyage;
    }

    public HandlingActivity in(Location location) {
      return new HandlingActivity(type, location, voyage);
    }
  }

}
