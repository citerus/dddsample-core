package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Date;

/**
 * An itinerary consists of one or more legs.
 */
public class Leg extends ValueObjectSupport<Leg> {

  private final Voyage voyage;
  private final Location loadLocation;
  private final Location unloadLocation;
  private final Date loadTime;
  private final Date unloadTime;

  // TODO hide this, use factory only
  public Leg(Voyage voyage, Location loadLocation, Location unloadLocation, Date loadTime, Date unloadTime) {
    Validate.notNull(voyage, "Voyage is required");
    Validate.notNull(loadLocation, "Load location is required");
    Validate.notNull(unloadLocation, "Unload location is required");
    Validate.notNull(loadTime, "Load time is required");
    Validate.notNull(unloadTime, "Unload time is required");
    Validate.isTrue(!loadLocation.sameAs(unloadLocation), "Load location can't be the same as unload location");
    // TODO enable this
    //Validate.isTrue(unloadTime.after(loadTime));

    this.voyage = voyage;
    this.loadLocation = loadLocation;
    this.unloadLocation = unloadLocation;
    this.loadTime = new Date(loadTime.getTime());
    this.unloadTime = new Date(unloadTime.getTime());
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
  public String toString() {
    return "Load in " + loadLocation + " at " + loadTime +
      " --- Unload in " + unloadLocation + " at " + unloadTime;
  }

  Leg() {
    // Needed by Hibernate
    voyage = null;
    loadLocation = unloadLocation = null;
    loadTime = unloadTime = null;
  }

}
