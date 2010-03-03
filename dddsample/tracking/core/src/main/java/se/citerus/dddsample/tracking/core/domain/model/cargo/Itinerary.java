package se.citerus.dddsample.tracking.core.domain.model.cargo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import java.util.*;

import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type.*;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.claimIn;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity.receiveIn;

/**
 * An itinerary.
 */
public class Itinerary extends ValueObjectSupport<Itinerary> {

  private final List<Leg> legs;

  /**
   * Constructor.
   *
   * @param legs List of legs for this itinerary.
   */
  public Itinerary(final List<Leg> legs) {
    Validate.notEmpty(legs);
    Validate.noNullElements(legs);

    // TODO
    // Validate that legs are in proper order, connected
    /*final Iterator<Leg> it = legs.iterator();
    Leg leg = it.next();
    while (it.hasNext()) {
      final Leg nextLeg = it.next();
      Validate.isTrue(leg.unloadTime().before(nextLeg.loadTime()));
      leg = nextLeg;
    }*/

    /*final ListIterator<Leg> lit = legs.listIterator();
    lit.next();
    while (lit.hasNext()) {
      Validate.isTrue(lit.previous().unloadTime().before(lit.next().loadTime()));
    }*/

    this.legs = legs;
  }

  public Itinerary(final Leg... legs) {
    this(Arrays.asList(legs));
  }

  /**
   * @return the legs of this itinerary, as an <b>immutable</b> list.
   */
  public List<Leg> legs() {
    return Collections.unmodifiableList(legs);
  }

  /**
   * @param rescheduledVoyage the voyage that has been rescheduled
   * @return A new itinerary which is a copy of the old one, adjusted for the delay of the given voyage.
   */
  public Itinerary withRescheduledVoyage(final Voyage rescheduledVoyage) {
    final List<Leg> newLegsList = new ArrayList<Leg>(this.legs.size());

    Leg lastAdded = null;
    for (Leg leg : legs) {
      if (leg.voyage().sameAs(rescheduledVoyage)) {
        Leg modifiedLeg = leg.withRescheduledVoyage(rescheduledVoyage);
        // This truncates the itinerary if the voyage rescheduling makes
        // it impossible to maintain the old unload-load chain.
        if (lastAdded != null && modifiedLeg.loadTime().before(lastAdded.unloadTime())) {
          break;
        }
        newLegsList.add(modifiedLeg);
      } else {
        newLegsList.add(leg);
      }
      lastAdded = leg;
    }

    return new Itinerary(newLegsList);
  }

  /**
   * Test if the given handling event was expected when executing this itinerary.
   *
   * @param handlingActivity Event to test.
   * @return <code>true</code> if the event is expected
   */
  boolean isExpectedActivity(final HandlingActivity handlingActivity) {
    return matchLeg(handlingActivity).leg() != null;
  }

  /**
   * @return The initial departure location.
   */
  Location initialLoadLocation() {
    return firstLeg().loadLocation();
  }

  /**
   * @return The final arrival location.
   */
  Location finalUnloadLocation() {
    return lastLeg().unloadLocation();
  }

  /**
   * @return Date when cargo arrives at final destination.
   */
  Date finalUnloadTime() {
    return new Date(lastLeg().unloadTime().getTime());
  }

  /**
   * @return A list of all locations on this itinerary.
   */
  List<Location> locations() {
    final List<Location> result = new ArrayList<Location>(legs.size() + 1);
    result.add(firstLeg().loadLocation());
    for (Leg leg : legs) {
      result.add(leg.unloadLocation());
    }
    return result;
  }

  /**
   * @param location a location
   * @return Load time at this location, or null if the location isn't on this itinerary.
   */
  public Date loadTimeAt(final Location location) {
    for (Leg leg : legs) {
      if (leg.loadLocation().sameAs(location)) {
        return leg.loadTime();
      }
    }

    return null;
  }

  /**
   * @return Estimated time of arrival
   */
  Date estimatedTimeOfArrival() {
    return new Date(finalUnloadTime().getTime());
  }

  /**
   * @param location a location
   * @return Unload time at this location, or null if the location isn't on this itinerary.
   */
  Date unloadTimeAt(final Location location) {
    for (Leg leg : legs) {
      if (leg.unloadLocation().sameAs(location)) {
        return leg.unloadTime();
      }
    }

    return null;
  }

  /**
   * @param previousActivity previous handling activity
   * @return Handling activity that succeds, or null if it can't be determined.
   */
  HandlingActivity activitySucceding(final HandlingActivity previousActivity) {
    if (previousActivity == null) {
      return receiveIn(firstLeg().loadLocation());
    } else {
      return deriveFromMatchingLeg(previousActivity, matchLeg(previousActivity).leg());
    }
  }

  /**
   * @param handlingActivity1 handling activity
   * @param handlingActivity2 handling activity
   * @return The activity which is strictly prior to the other, according to the itinerary, or null if neither is strictly prior.
   */
  HandlingActivity strictlyPriorOf(final HandlingActivity handlingActivity1, final HandlingActivity handlingActivity2) {
    final LegActivityMatch match1 = matchLeg(handlingActivity1);
    final LegActivityMatch match2 = matchLeg(handlingActivity2);
    final int compared = match1.compareTo(match2);

    if (compared < 0) {
      return match1.handlingActivity();
    } else if (compared > 0) {
      return match2.handlingActivity();
    } else {
      return null;
    }
  }

  /**
   * @param leg leg
   * @return The next leg, or null if this is the last leg.
   */
  Leg nextLeg(final Leg leg) {
    for (Iterator<Leg> it = legs.iterator(); it.hasNext();) {
      if (it.next().sameValueAs(leg)) {
        return it.hasNext() ? it.next() : null;
      }
    }

    return null;
  }

  /**
   * @param handlingActivity handling activity
   * @return The leg match of this handling activity. Never null.
   */
  LegActivityMatch matchLeg(final HandlingActivity handlingActivity) {
    if (handlingActivity == null) {
      return LegActivityMatch.noMatch(handlingActivity, this);
    } else if (handlingActivity.type() == RECEIVE) {
      return LegActivityMatch.ifLoadLocationSame(firstLeg(), handlingActivity, this);
    } else if (handlingActivity.type() == CLAIM) {
      return LegActivityMatch.ifUnloadLocationSame(lastLeg(), handlingActivity, this);
    } else {
      return findLegMatchingActivity(handlingActivity);
    }
  }

  /**
   * @return The first leg on the itinerary.
   */
  Leg firstLeg() {
    return legs.get(0);
  }

  /**
   * @return The last leg on the itinerary.
   */
  public Leg lastLeg() {
    return legs.get(legs.size() - 1);
  }


  Itinerary truncatedAfter(final Location location) {
    final List<Leg> newLegs = new ArrayList<Leg>();

    for (Leg leg : legs) {
      if (leg.voyage().locations().contains(location)) {
        newLegs.add(Leg.deriveLeg(leg.voyage(), leg.loadLocation(), location));
        break;
      } else {
        newLegs.add(leg);
        if (leg.unloadLocation().sameAs(location)) {
          break;
        }
      }
    }

    return new Itinerary(newLegs);
  }

  Itinerary withLeg(final Leg leg) {
    final List<Leg> newLegs = new ArrayList<Leg>(legs.size() + 1);
    newLegs.addAll(legs);
    newLegs.add(leg);

    return new Itinerary(newLegs);
  }

  Itinerary appendBy(final Itinerary other) {
    final List<Leg> newLegs = new ArrayList<Leg>(this.legs.size() + other.legs.size());
    newLegs.addAll(this.legs);
    newLegs.addAll(other.legs);

    return new Itinerary(newLegs);
  }
  
  private LegActivityMatch findLegMatchingActivity(final HandlingActivity handlingActivity) {
    for (Leg leg : legs) {
      if (leg.matchesActivity(handlingActivity)) {
        return LegActivityMatch.match(leg, handlingActivity, this);
      }
    }

    return LegActivityMatch.noMatch(handlingActivity, this);
  }

  private HandlingActivity deriveFromMatchingLeg(final HandlingActivity handlingActivity, final Leg matchingLeg) {
    if (matchingLeg == null) {
      return null;
    } else {
      if (handlingActivity.type() == LOAD) {
        return matchingLeg.deriveUnloadActivity();
      } else if (handlingActivity.type() == UNLOAD) {
        return deriveFromNextLeg(nextLeg(matchingLeg));
      } else {
        // Will only derive from load and unload within the itinerary context
        return null;
      }
    }
  }

  private HandlingActivity deriveFromNextLeg(final Leg nextLeg) {
    if (nextLeg == null) {
      return claimIn(lastLeg().unloadLocation());
    } else {
      return nextLeg.deriveLoadActivity();
    }
  }

  @Override
  public String toString() {
    return StringUtils.join(legs, "\n");
  }

  Itinerary() {
    // Needed by Hibernate
    legs = Collections.emptyList();
  }

}
