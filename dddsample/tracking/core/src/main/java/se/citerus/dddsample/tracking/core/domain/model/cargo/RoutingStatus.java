package se.citerus.dddsample.tracking.core.domain.model.cargo;

import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObject;


/**
 * The different routing statuses of a cargo.
 */
public enum RoutingStatus implements ValueObject<RoutingStatus> {
  NOT_ROUTED, ROUTED, MISROUTED;

  @Override
  public boolean sameValueAs(final RoutingStatus other) {
    return this.equals(other);
  }

  public static RoutingStatus derivedFrom(final Itinerary itinerary, final RouteSpecification routeSpecification) {
    if (itinerary == null) {
      return NOT_ROUTED;
    } else {
      if (routeSpecification.isSatisfiedBy(itinerary)) {
        return ROUTED;
      } else {
        return MISROUTED;
      }
    }
  }

}
