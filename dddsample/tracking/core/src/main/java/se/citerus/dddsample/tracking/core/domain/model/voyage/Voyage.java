package se.citerus.dddsample.tracking.core.domain.model.voyage;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.patterns.entity.EntitySupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * A Voyage.
 */
public class Voyage extends EntitySupport<Voyage,VoyageNumber> {

  private final VoyageNumber voyageNumber;
  private Schedule schedule;

  // Null object pattern
  public static final Voyage NONE = new Voyage(new VoyageNumber(""), Schedule.EMPTY);

  public Voyage(final VoyageNumber voyageNumber, final Schedule schedule) {
    Validate.notNull(voyageNumber, "Voyage number is required");
    Validate.notNull(schedule, "Schedule is required");

    this.voyageNumber = voyageNumber;
    this.schedule = schedule;
  }

  @Override
  public VoyageNumber identity() {
    return voyageNumber;
  }
  
  /**
   * @return Voyage number.
   */
  public VoyageNumber voyageNumber() {
    return voyageNumber;
  }

  /**
   * @return Schedule.
   */
  public Schedule schedule() {
    return schedule;
  }

  /**
   * @param location         location from where the rescheduled departure happens.
   * @param newDepartureTime new departure time
   */
  public void departureRescheduled(final Location location, final Date newDepartureTime) {
    final List<CarrierMovement> carrierMovements = new ArrayList<CarrierMovement>();

    for (CarrierMovement carrierMovement : schedule.carrierMovements()) {
      if (carrierMovement.departureLocation().sameAs(location)) {
        carrierMovements.add(carrierMovement.withDepartureTime(newDepartureTime));
      } else {
        carrierMovements.add(carrierMovement);
      }
    }

    this.schedule = new Schedule(carrierMovements);
  }


  public Location arrivalLocationAfterDepartureFrom(final Location departureLocation) {
    for (CarrierMovement carrierMovement : schedule.carrierMovements()) {
      if (carrierMovement.departureLocation().sameAs(departureLocation)) {
        return carrierMovement.arrivalLocation();
      }
    }

    return Location.NONE;
  }

  public List<Location> locations() {
    final List<Location> locations = new ArrayList<Location>();
    final Iterator<CarrierMovement> it = schedule.carrierMovements().iterator();

    for (; it.hasNext(); ) {
      final CarrierMovement carrierMovement = it.next();
      locations.add(carrierMovement.departureLocation());

      if (!it.hasNext()) {
        locations.add(carrierMovement.arrivalLocation());
      }
    }

    return unmodifiableList(locations);
  }
  
  @Override
  public String toString() {
    return voyageNumber.stringValue();
  }

  Voyage() {
    // Needed by Hibernate
    voyageNumber = null;
  }

  /**
   * Builder pattern is used for incremental construction
   * of a Voyage aggregate. This serves as an aggregate factory.
   */
  public static final class Builder {

    private final List<CarrierMovement> carrierMovements = new ArrayList<CarrierMovement>();
    private final VoyageNumber voyageNumber;
    private Location currentDepartureLocation;

    public Builder(final VoyageNumber voyageNumber, final Location initialDepartureLocation) {
      Validate.notNull(voyageNumber, "Voyage number is required");
      Validate.notNull(initialDepartureLocation, "Departure location is required");

      this.voyageNumber = voyageNumber;
      this.currentDepartureLocation = initialDepartureLocation;
    }

    public Builder addMovement(final Location arrivalLocation, final Date departureTime, final Date arrivalTime) {
      carrierMovements.add(new CarrierMovement(currentDepartureLocation, arrivalLocation, departureTime, arrivalTime));
      // Next departure location is the same as this arrival location
      this.currentDepartureLocation = arrivalLocation;
      return this;
    }

    public Voyage build() {
      return new Voyage(voyageNumber, new Schedule(carrierMovements));
    }

  }

}
