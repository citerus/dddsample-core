package se.citerus.dddsample.tracking.core.domain.model.shared;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType.*;

/**
 * A handling activity represents how and where a cargo can be handled,
 * and can be used to express predictions about what is expected to
 * happen to a cargo in the future.
 */
public class HandlingActivity extends ValueObjectSupport<HandlingActivity> {

  private final HandlingActivityType type;
  private final Location location;
  private final Voyage voyage;

  public HandlingActivity(final HandlingActivityType type, final Location location) {
    Validate.notNull(type, "Handling event type is required");
    Validate.notNull(location, "Location is required");

    this.type = type;
    this.location = location;
    this.voyage = null;
  }

  public HandlingActivity(final HandlingActivityType type, final Location location, final Voyage voyage) {
    Validate.notNull(type, "Handling event type is required");
    Validate.notNull(location, "Location is required");
    Validate.notNull(voyage, "Voyage is required");

    this.type = type;
    this.location = location;
    this.voyage = voyage;
  }

  /**
   * @return Type of handling
   */
  public HandlingActivityType type() {
    return type;
  }

  /**
   * @return Location
   */
  public Location location() {
    return location;
  }

  /**
   * @return Voyage
   */
  public Voyage voyage() {
    return voyage;
  }

  /**
   * @return A copy of this activity
   */
  public HandlingActivity copy() {
    return new HandlingActivity(type, location, voyage);
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

  public static InLocation loadOnto(final Voyage voyage) {
    return new InLocation(LOAD, voyage);
  }

  public static InLocation unloadOff(final Voyage voyage) {
    return new InLocation(UNLOAD, voyage);
  }

  public static HandlingActivity receiveIn(final Location location) {
    return new HandlingActivity(RECEIVE, location);
  }

  public static HandlingActivity claimIn(final Location location) {
    return new HandlingActivity(CLAIM, location);
  }

  public static HandlingActivity customsIn(final Location location) {
    return new HandlingActivity(CUSTOMS, location);
  }

  public static class InLocation {
    private final HandlingActivityType type;
    private final Voyage voyage;

    public InLocation(final HandlingActivityType type, final Voyage voyage) {
      this.type = type;
      this.voyage = voyage;
    }

    public HandlingActivity in(final Location location) {
      return new HandlingActivity(type, location, voyage);
    }
  }

}
