package se.citerus.dddsample.domain.model.voyage;

import jakarta.persistence.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.DomainEntity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Voyage.
 */
@Entity(name = "Voyage")
@Table(name = "Voyage")
public class Voyage implements DomainEntity<Voyage> {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(name = "voyage_number", unique = true)
  private String voyageNumber;

  @OneToMany(cascade = CascadeType.ALL)
  @JoinColumn(name = "voyage_id")
  private List<CarrierMovement> carrierMovements;

  protected Voyage() {
    // Needed by Hibernate
  }

  // Null object pattern
  @Transient
  public static final Voyage NONE = new Voyage(new VoyageNumber(""), Schedule.EMPTY);

    public Voyage(final VoyageNumber voyageNumber, final Schedule schedule) {
    Objects.requireNonNull(voyageNumber, "Voyage number is required");
    Objects.requireNonNull(schedule, "Schedule is required");

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



  /**
   * Builder pattern is used for incremental construction
   * of a Voyage aggregate. This serves as an aggregate factory.
   */
  public static final class Builder {

    private final List<CarrierMovement> carrierMovements = new ArrayList<>();
    private final VoyageNumber voyageNumber;
    private Location departureLocation;

    public Builder(final VoyageNumber voyageNumber, final Location departureLocation) {
      Objects.requireNonNull(voyageNumber, "Voyage number is required");
      Objects.requireNonNull(departureLocation, "Departure location is required");

      this.voyageNumber = voyageNumber;
      this.departureLocation = departureLocation;
    }

    public Builder addMovement(Location arrivalLocation, Instant departureTime, Instant arrivalTime) {
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
