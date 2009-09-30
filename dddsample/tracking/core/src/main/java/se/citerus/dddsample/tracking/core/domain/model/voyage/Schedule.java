package se.citerus.dddsample.tracking.core.domain.model.voyage;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A voyage schedule.
 */
public class Schedule extends ValueObjectSupport<Schedule> {

  private final List<CarrierMovement> carrierMovements;

  public static final Schedule EMPTY = new Schedule();

  Schedule(final List<CarrierMovement> carrierMovements) {
    Validate.notNull(carrierMovements);
    Validate.noNullElements(carrierMovements);
    Validate.notEmpty(carrierMovements);

    this.carrierMovements = carrierMovements;
  }

  /**
   * @return Carrier movements.
   */
  public List<CarrierMovement> carrierMovements() {
    return Collections.unmodifiableList(carrierMovements);
  }

  /**
   * @param location location of departure
   * @return Date of departure from this location, or null if it's not part of the voyage.
   */
  public Date departureTimeAt(final Location location) {
    for (CarrierMovement movement : carrierMovements) {
      if (movement.departureLocation().sameAs(location)) {
        return movement.departureTime();
      }
    }
    return null;
  }

  /**
   * @param location location of arrival
   * @return Date of arrival at this location, or null if it's not part of the voyage.
   */
  public Date arrivalTimeAt(final Location location) {
    for (CarrierMovement movement : carrierMovements) {
      if (movement.arrivalLocation().sameAs(location)) {
        return movement.arrivalTime();
      }
    }
    return null;
  }

  Schedule() {
    // Needed by Hibernate
    carrierMovements = null;
  }
}
