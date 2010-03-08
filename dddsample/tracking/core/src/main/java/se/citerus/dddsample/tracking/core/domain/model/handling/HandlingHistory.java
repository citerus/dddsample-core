package se.citerus.dddsample.tracking.core.domain.model.handling;

import org.apache.commons.lang.Validate;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.patterns.valueobject.ValueObject;

import java.util.*;

import static java.util.Collections.sort;

/**
 * The handling history of a cargo.
 */
public class HandlingHistory implements ValueObject<HandlingHistory> {

  private final List<HandlingEvent> handlingEvents;
  private final Cargo cargo;

  public static HandlingHistory emptyForCargo(final Cargo cargo) {
    return new HandlingHistory(cargo);
  }

  public static HandlingHistory fromEvents(final Collection<HandlingEvent> handlingEvents) {
    return new HandlingHistory(handlingEvents);
  }

  private HandlingHistory(final Cargo cargo) {
    Validate.notNull(cargo, "Cargo is required");
    this.cargo = cargo;
    handlingEvents = Collections.emptyList();
  }

  private HandlingHistory(final Collection<HandlingEvent> handlingEvents) {
    Validate.notEmpty(handlingEvents, "Handling events are required");

    this.cargo = uniqueCargo(handlingEvents);
    this.handlingEvents = new ArrayList<HandlingEvent>(handlingEvents);
  }

  private Cargo uniqueCargo(final Collection<HandlingEvent> handlingEvents) {
    final Iterator<HandlingEvent> it = handlingEvents.iterator();
    final Cargo firstCargo = it.next().cargo();
    Validate.notNull(firstCargo, "Cargo is required");

    while (it.hasNext()) {
      final Cargo nextCargo = it.next().cargo();
      Validate.isTrue(firstCargo.sameAs(nextCargo),
        "A handling history can only contain handling events for a unique cargo. " +
          "First event is for cargo " + firstCargo + ", also discovered cargo " + nextCargo
      );
    }

    return firstCargo;
  }

  /**
   * @return A distinct list (no duplicate registrations) of handling events, ordered by completion time.
   */
  public List<HandlingEvent> distinctEventsByCompletionTime() {
    final List<HandlingEvent> ordered = new ArrayList<HandlingEvent>(
      new HashSet<HandlingEvent>(handlingEvents)
    );
    sort(ordered, BY_COMPLETION_TIME_COMPARATOR);
    return Collections.unmodifiableList(ordered);
  }

  /**
   * @return Filter a list of handling events, returning only the LOAD and UNLOAD events.
   */
  public List<HandlingEvent> physicalHandlingEvents(List<HandlingEvent> unfilteredEvents) {
    final List<HandlingEvent> filtered = new ArrayList<HandlingEvent>();
    for (HandlingEvent event : unfilteredEvents) {
      if (event.type().equals(HandlingActivityType.RECEIVE)) filtered.add(event);
      if (event.type().equals(HandlingActivityType.LOAD)) filtered.add(event);
      if (event.type().equals(HandlingActivityType.UNLOAD)) filtered.add(event);
      if (event.type().equals(HandlingActivityType.CLAIM)) filtered.add(event);
    }
    return Collections.unmodifiableList(filtered);
  }

  /**
   * @return Most recently completed event, or null if the handling history is empty.
   */
  public HandlingEvent mostRecentlyCompletedEvent() {
    final List<HandlingEvent> distinctEvents = distinctEventsByCompletionTime();
    if (distinctEvents.isEmpty()) {
      return null;
    } else {
      return distinctEvents.get(distinctEvents.size() - 1);
    }
  }

  /**
   * @return Most recently completed load or unload, or null if there have been none.
   */
  public HandlingEvent mostRecentPhysicalHandling() {
    final List<HandlingEvent> loadsAndUnloads = physicalHandlingEvents(distinctEventsByCompletionTime());
    if (loadsAndUnloads.isEmpty()) {
      return null;
    } else {
      return loadsAndUnloads.get(loadsAndUnloads.size() - 1);
    }
  }

  /**
   * @return The cargo to which this handling history refers.
   */
  public Cargo cargo() {
    return cargo;
  }

  @Override
  public boolean sameValueAs(HandlingHistory other) {
    return other != null && this.handlingEvents.equals(other.handlingEvents);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final HandlingHistory other = (HandlingHistory) o;
    return sameValueAs(other);
  }

  @Override
  public int hashCode() {
    return handlingEvents.hashCode();
  }

  private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR =
    new Comparator<HandlingEvent>() {
      public int compare(final HandlingEvent he1, final HandlingEvent he2) {
        return he1.completionTime().compareTo(he2.completionTime());
      }
    };
}
