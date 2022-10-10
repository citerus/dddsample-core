package se.citerus.dddsample.domain.model.voyage;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.Entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Voyage.
 */
@javax.persistence.Entity(name = "Voyage")
@Table(name = "Voyage")
public class Voyage implements Entity<Voyage> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  public long id;

  @Column(name = "voyage_number", unique = true)
  public String voyageNumber;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "voyage_id")
  public List<CarrierMovement> carrierMovements;

  // Null object pattern
  public static final Voyage NONE = new Voyage(
    new VoyageNumber(""), Schedule.EMPTY
  );

  public Voyage(final VoyageNumber voyageNumber, final Schedule schedule) {
    Validate.notNull(voyageNumber, "Voyage number is required");
    Validate.notNull(schedule, "Schedule is required");

    this.voyageNumber = voyageNumber.idString();
    this.carrierMovements = schedule.carrierMovements();
  }

  /**
   * @return Voyage number.
   */
  public VoyageNumber voyageNumber() {
    return new VoyageNumber(voyageNumber);
  }

  /**
   * @return Schedule.
   */
  public Schedule schedule() {
    return new Schedule(carrierMovements);
  }

  @Override
  public int hashCode() {
    return voyageNumber.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    if (!(o instanceof Voyage)) return false;

    final Voyage that = (Voyage) o;

    return sameIdentityAs(that);
  }

  @Override
  public boolean sameIdentityAs(Voyage other) {
    return other != null && this.voyageNumber().sameValueAs(other.voyageNumber());
  }

  @Override
  public String toString() {
    return "Voyage " + voyageNumber;
  }

  Voyage() {
    // Needed by Hibernate
  }

  /**
   * Builder pattern is used for incremental construction
   * of a Voyage aggregate. This serves as an aggregate factory. 
   */
  public static final class Builder {

    private final List<CarrierMovement> carrierMovements = new ArrayList<>();
    private final VoyageNumber voyageNumber;
    private Location departureLocation;

    public Builder(final VoyageNumber voyageNumber, final Location departureLocation) {
      Validate.notNull(voyageNumber, "Voyage number is required");
      Validate.notNull(departureLocation, "Departure location is required");

      this.voyageNumber = voyageNumber;
      this.departureLocation = departureLocation;
    }

    public Builder addMovement(Location arrivalLocation, Date departureTime, Date arrivalTime) {
      carrierMovements.add(new CarrierMovement(departureLocation, arrivalLocation, departureTime, arrivalTime));
      // Next departure location is the same as this arrival location
      this.departureLocation = arrivalLocation;
      return this;
    }

    public Voyage build() {
      return new Voyage(voyageNumber, new Schedule(carrierMovements));
    }

  }

}
