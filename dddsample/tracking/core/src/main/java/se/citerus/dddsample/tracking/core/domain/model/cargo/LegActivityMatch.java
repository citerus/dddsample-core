package se.citerus.dddsample.tracking.core.domain.model.cargo;

import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivity;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObjectSupport;

import static se.citerus.dddsample.tracking.core.domain.model.cargo.LegActivityMatch.LegEnd.*;

class LegActivityMatch extends ValueObjectSupport<LegActivityMatch> implements Comparable<LegActivityMatch> {

  private final Leg leg;
  private final LegEnd legEnd;
  private final HandlingActivity handlingActivity;
  private final Itinerary itinerary;

  enum LegEnd { LOAD_END, UNLOAD_END, NO_END }

  private LegActivityMatch(final Leg leg, final LegEnd legEnd, final HandlingActivity handlingActivity, final Itinerary itinerary) {
    this.leg = leg;
    this.legEnd = legEnd;
    this.handlingActivity = handlingActivity;
    this.itinerary = itinerary;
  }

  static LegActivityMatch match(final Leg leg, final HandlingActivity handlingActivity, final Itinerary itinerary) {
    switch (handlingActivity.type()) {
      case RECEIVE:
      case LOAD:
        return new LegActivityMatch(leg, LOAD_END, handlingActivity, itinerary);
      case UNLOAD:
      case CLAIM:
      case CUSTOMS:
        return new LegActivityMatch(leg, UNLOAD_END, handlingActivity, itinerary);
      default:
        return noMatch(handlingActivity, itinerary);
    }
  }

  static LegActivityMatch ifLoadLocationSame(final Leg leg, final HandlingActivity handlingActivity, final Itinerary itinerary) {
    if (leg.loadLocation().sameAs(handlingActivity.location())) {
      return new LegActivityMatch(leg, LOAD_END, handlingActivity, itinerary);
    } else {
      return noMatch(handlingActivity, itinerary);
    }
  }

  static LegActivityMatch ifUnloadLocationSame(final Leg leg, final HandlingActivity handlingActivity, final Itinerary itinerary) {
    if (leg.unloadLocation().sameAs(handlingActivity.location())) {
      return new LegActivityMatch(leg, UNLOAD_END, handlingActivity, itinerary);
    } else {
      return noMatch(handlingActivity, itinerary);
    }
  }

  static LegActivityMatch noMatch(final HandlingActivity handlingActivity, final Itinerary itinerary) {
    return new LegActivityMatch(null, NO_END, handlingActivity, itinerary);
  }

  Leg leg() {
    return leg;
  }

  HandlingActivity handlingActivity() {
    return handlingActivity;
  }

  @Override
  public int compareTo(final LegActivityMatch other) {
    final Integer thisLegIndex = itinerary.legs().indexOf(this.leg);
    final Integer otherLegIndex = itinerary.legs().indexOf(other.leg);

    if (thisLegIndex.equals(otherLegIndex)) {
      return this.legEnd.compareTo(other.legEnd);
    } else {
      return toPositive(thisLegIndex).compareTo(toPositive(otherLegIndex));
    }
  }

  private Integer toPositive(final Integer thisLegIndex) {
    return thisLegIndex >= 0 ? thisLegIndex : Integer.MAX_VALUE;
  }

  @Override
  public String toString() {
    if (legEnd == NO_END) {
      return "No match"; 
    } else {            
      return "Activity " + handlingActivity + " matches leg " + leg + " at " + legEnd;
    }
  }

}
