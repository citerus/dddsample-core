package se.citerus.dddsample.domain;

import org.apache.commons.lang.Validate;

import javax.persistence.*;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

/**
 *
 */
@Entity
public class Itinerary {

  @Id
  @GeneratedValue
  private Long id;

  @OneToMany
  @JoinColumn(name = "itinerary_id")
  private List<Leg> legs = Collections.emptyList();

  public static final Itinerary EMPTY_ITINERARY = new Itinerary();

  public Itinerary(List<Leg> legs) {
    Validate.notEmpty(legs);
    Validate.noNullElements(legs);
    this.legs = legs;
  }

  public Itinerary(Leg... legs) {
    this(Arrays.asList(legs));
  }

  public List<Leg> legs() {
    return legs;
  }

  /**
   * Test if the given handling event is expected when executing this itinerary.
   *
   * @param event Event to test.
   * @return <code>true</code> if the event is expected
   */
  public boolean isExpected(HandlingEvent event) {
    if (legs.isEmpty()) {
      return true;
    }

    if (event.type() == HandlingEvent.Type.RECEIVE) {
      //Check that the first leg's origin is the event's location
      Leg leg = legs.get(0);
      return (leg.from().equals(event.location()));
    }

    if (event.type() == HandlingEvent.Type.LOAD) {
      //Check that the there is one leg with same from location and carrier movement
      for (Leg leg : legs) {
        if (leg.from().equals(event.location())
           && leg.carrierMovementId().equals(event.carrierMovement().carrierId()))
          return true;
      }
      return false;
    }

    if (event.type() == HandlingEvent.Type.UNLOAD) {
      //Check that the there is one leg with same to loc and carrier movement
      for (Leg leg : legs) {
        if (leg.to().equals(event.location())
           && leg.carrierMovementId().equals(event.carrierMovement().carrierId()))
          return true;
      }
      return false;
    }

    if (event.type() == HandlingEvent.Type.CLAIM) {
      //Check that the last leg's destination is from the event's location
      Leg leg = legs.get(legs.size() - 1);
      return (leg.to().equals(event.location()));
    }

    //HandlingEvent.Type.CUSTOMS;
    return true;
  }

  /**
   * @param other itinerary to compare
   * @return <code>true</code> if the legs in this and the other itinerary are all equal.
   */
  public boolean sameValueAs(Itinerary other) {
    return other != null && legs.equals(other.legs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Itinerary itinerary = (Itinerary) o;

    return sameValueAs(itinerary);
  }

  @Override
  public int hashCode() {
    return legs.hashCode();
  }

  Itinerary() {
    // Needed by Hibernate
  }
}
