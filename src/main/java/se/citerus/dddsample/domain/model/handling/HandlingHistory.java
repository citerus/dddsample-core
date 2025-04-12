package se.citerus.dddsample.domain.model.handling;

import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.shared.ValueObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The handling history of a cargo.
 */
public class HandlingHistory implements ValueObject<HandlingHistory> {

    private final List<HandlingEvent> handlingEvents;

    public static final HandlingHistory EMPTY = new HandlingHistory(Collections.<HandlingEvent>emptyList());

    public HandlingHistory(Collection<HandlingEvent> handlingEvents) {
        Objects.requireNonNull(handlingEvents, "Handling events are required");

        this.handlingEvents = new ArrayList<>(handlingEvents);
    }

    /**
     * @return A distinct list (no duplicate registrations) of handling events, ordered by completion time.
     */
    public List<HandlingEvent> distinctEventsByCompletionTime() {
        final List<HandlingEvent> ordered = new ArrayList<>(
                new HashSet<>(handlingEvents)
        );
        ordered.sort(BY_COMPLETION_TIME_COMPARATOR);
        return Collections.unmodifiableList(ordered);
    }

    /**
     * @return Most recently completed event, or null if the delivery history is empty.
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
     * Filters handling history events to remove events for unrelated cargo.
     * @param trackingId the trackingId of the cargo to filter events for.
     * @return A new handling history with events matching the supplied tracking id.
     */
    public HandlingHistory filterOnCargo(TrackingId trackingId) {
        List<HandlingEvent> events = handlingEvents.stream()
                .filter(he -> he.cargo().trackingId().sameValueAs(trackingId))
                .collect(Collectors.toList());
        return new HandlingHistory(events);
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
            Comparator.comparing(HandlingEvent::completionTime);

}
