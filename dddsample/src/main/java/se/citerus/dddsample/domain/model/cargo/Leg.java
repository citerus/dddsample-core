package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Date;

/**
 * An itinerary consists of one or more legs.
 */
public class Leg implements ValueObject<Leg> {

  private Voyage voyage;
  private Location loadLocation;
  private Location unloadLocation;
  private Date loadTime;
  private Date unloadTime;

  // TODO hide this, use factory
  public Leg(Voyage voyage, Location loadLocation, Location unloadLocation, Date loadTime, Date unloadTime) {
    Validate.notNull(voyage, "Voyage is required");
    Validate.notNull(loadLocation, "Load location is required");
    Validate.notNull(unloadLocation, "Unload location is required");
    Validate.isTrue(!loadLocation.sameIdentityAs(unloadLocation));
    // TODO use a minimum time between unloading and loading?
    Validate.isTrue(unloadTime.after(loadTime));

    this.voyage = voyage;
    this.loadLocation = loadLocation;
    this.unloadLocation = unloadLocation;
    this.loadTime = loadTime;
    this.unloadTime = unloadTime;
  }

  /**
   * This simple factory takes the Leg's times from the state of the
   * Voyage as of the time of construction.
   * A fuller version might also factor operational time
   * in the port. For example, average unload time of the
   * unloadLocation could be added to the eta of the vessel
   * schedule, providing an estimated unload time.
   * In a real system, the estimation of the unload time
   * might be more complex.
   *
   * @param voyage         voyage
   * @param loadLocation   load location
   * @param unloadLocation unload location
   * @return A leg on this voyage between the given locations.
   */
  public static Leg deriveLeg(Voyage voyage, Location loadLocation, Location unloadLocation) {
    return new Leg(voyage, loadLocation, unloadLocation, voyage.schedule().departureTimeAt(loadLocation), voyage.schedule().arrivalTimeAt(unloadLocation));
  }

  public Voyage voyage() {
    return voyage;
  }

  public Location loadLocation() {
    return loadLocation;
  }

  public Location unloadLocation() {
    return unloadLocation;
  }

  public Date loadTime() {
    return new Date(loadTime.getTime());
  }

  public Date unloadTime() {
    return new Date(unloadTime.getTime());
  }

  /**
   * @param voyage voyage
   * @return A new leg with the same load and unload locations, but with updated load/unload times.
   */
  Leg withRescheduledVoyage(final Voyage voyage) {
    return Leg.deriveLeg(voyage, loadLocation, unloadLocation);
  }

  @Override
  public boolean sameValueAs(final Leg other) {
    return other != null && new EqualsBuilder().
      append(this.voyage, other.voyage).
      append(this.loadLocation, other.loadLocation).
      append(this.unloadLocation, other.unloadLocation).
      append(this.loadTime, other.loadTime).
      append(this.unloadTime, other.unloadTime).
      isEquals();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Leg leg = (Leg) o;

    return sameValueAs(leg);
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().
      append(voyage).
      append(loadLocation).
      append(unloadLocation).
      append(loadTime).
      append(unloadTime).
      toHashCode();
  }

  @Override
  public String toString() {
    return "Load in " + loadLocation + " at " + loadTime +
      ", unload in " + unloadLocation + " at " + unloadTime;
  }

  Leg() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  private Long id;

}
