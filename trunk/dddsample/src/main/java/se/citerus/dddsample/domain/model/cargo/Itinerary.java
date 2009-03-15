package se.citerus.dddsample.domain.model.cargo;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * An itinerary.
 *
 */
public class Itinerary implements ValueObject<Itinerary> {

  private List<Leg> legs = Collections.emptyList();

  static final Itinerary EMPTY_ITINERARY = new Itinerary();
  private static final Date END_OF_DAYS = new Date(Long.MAX_VALUE);

  /**
   * Constructor.
   *
   * @param legs List of legs for this itinerary.
   */
  public Itinerary(final List<Leg> legs) {
    Validate.notEmpty(legs);
    Validate.noNullElements(legs);

    this.legs = legs;
  }

  /**
   * @return the legs of this itinerary, as an <b>immutable</b> list.
   */
  public List<Leg> legs() {
    return Collections.unmodifiableList(legs);
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
      return (leg.loadLocation().equals(event.location()));
    }

    if (event.type() == HandlingEvent.Type.LOAD) {
      //Check that the there is one leg with same load location and voyage
      for (Leg leg : legs) {
        if (leg.loadLocation().sameIdentityAs(event.location()) &&
            leg.voyage().sameIdentityAs(event.voyage()))
          return true;
      }
      return false;
    }

    if (event.type() == HandlingEvent.Type.UNLOAD) {
      //Check that the there is one leg with same unload location and voyage
      for (Leg leg : legs) {
        if (leg.unloadLocation().equals(event.location()) &&
            leg.voyage().equals(event.voyage()))
          return true;
      }
      return false;
    }

    if (event.type() == HandlingEvent.Type.CLAIM) {
      //Check that the last leg's destination is from the event's location
      final Leg leg = lastLeg();
      return (leg.unloadLocation().equals(event.location()));
    }

    //HandlingEvent.Type.CUSTOMS;
    return true;
  }

  /**
   * @return The initial departure location.
   */
  Location initialDepartureLocation() {
     if (legs.isEmpty()) {
       return Location.UNKNOWN;
     } else {
       return legs.get(0).loadLocation();
     }
  }

  /**
   * @return The final arrival location.
   */
  Location finalArrivalLocation() {
    if (legs.isEmpty()) {
      return Location.UNKNOWN;
    } else {
      return lastLeg().unloadLocation();
    }
  }

  /**
   * @return Date when cargo arrives at final destination.
   */
  Date finalArrivalDate() {
    final Leg lastLeg = lastLeg();

    if (lastLeg == null) {
      return new Date(END_OF_DAYS.getTime());
    } else {
      return new Date(lastLeg.unloadTime().getTime());
    }
  }

  /**
   * @return The last leg on the itinerary.
   */
  Leg lastLeg() {
    if (legs.isEmpty()) {
      return null;
    } else {
      return legs.get(legs.size() - 1);
    }
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
