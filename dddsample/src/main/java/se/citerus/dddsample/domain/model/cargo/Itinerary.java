package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.ValueObject;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An itinerary.
 * 
 */
public class Itinerary implements ValueObject<Itinerary> {

  private Cargo cargo;
  private List<Leg> legs = Collections.emptyList();

  static final Itinerary EMPTY_ITINERARY = new Itinerary() {
    void setCargo(Cargo cargo) {
      // Noop
    }
  };

  /**
   * Constructor.
   *
   * @param legs List of legs for this itinerary.
   */
  public Itinerary(final List<Leg> legs) {
    Validate.notEmpty(legs);
    Validate.noNullElements(legs);
    
    this.legs = Collections.unmodifiableList(legs);
  }

  /**
   * For maintaing referential integrity inside the Cargo aggregate,
   * with package level visibility. 
   *
   * @see Cargo#assignToRoute(Itinerary)
   *  
   * @param cargo the cargo that this itinerary is for
   */
  void setCargo(Cargo cargo) {
    this.cargo = cargo;
  }

  /**
   * @return the legs of this itinerary, as an <b>immutable</b> list.
   */
  public List<Leg> legs() {
    return legs;
  }

  /**
   * Test if the given handling event is expected when executing this itinerary.
   *
   * @param event Event to test.
   * @return <code>true</code> if the event is expected
   */
  public boolean isExpected(final HandlingEvent event) {
    if (legs.isEmpty()) {
      return true;
    }

    if (event.type() == HandlingEvent.Type.RECEIVE) {
      //Check that the first leg's origin is the event's location
      final Leg leg = legs.get(0);
      return (leg.from().equals(event.location()));
    }

    if (event.type() == HandlingEvent.Type.LOAD) {
      //Check that the there is one leg with same from location and carrier movement
      for (Leg leg : legs) {
        if (leg.from().equals(event.location())
          && leg.carrierMovement().equals(event.carrierMovement()))
          return true;
      }
      return false;
    }

    if (event.type() == HandlingEvent.Type.UNLOAD) {
      //Check that the there is one leg with same to loc and carrier movement
      for (Leg leg : legs) {
        if (leg.to().equals(event.location())
          && leg.carrierMovement().equals(event.carrierMovement()))
          return true;
      }
      return false;
    }

    if (event.type() == HandlingEvent.Type.CLAIM) {
      //Check that the last leg's destination is from the event's location
      final Leg leg = legs.get(legs.size() - 1);
      return (leg.to().equals(event.location()));
    }

    //HandlingEvent.Type.CUSTOMS;
    return true;
  }

  /**
   * @param other itinerary to compare
   * @return <code>true</code> if the legs in this and the other itinerary are all equal.
   */
  @Override
  public boolean sameValueAs(final Itinerary other) {
    return other != null && legs.equals(other.legs);
  }

  @Override
  public Itinerary copy() {
    final List<Leg> legsCopy = new ArrayList<Leg>(legs.size());
    for (Leg leg : legs) {
      legsCopy.add(leg.copy());
    }
    return new Itinerary(legsCopy);
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Itinerary itinerary = (Itinerary) o;

    return sameValueAs(itinerary);
  }

  @Override
  public int hashCode() {
    return legs.hashCode();
  }

  Itinerary() {
    // Needed by Hibernate
  }

  // Auto-generated surrogate key
  private Long id;

}
