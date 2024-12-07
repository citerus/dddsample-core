package se.citerus.dddsample.interfaces.handling;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.lang.NonNull;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.io.Serializable;
import java.time.Instant;

/**
 * This is a simple transfer object for passing incoming handling event
 * registration attempts to proper the registration procedure.
 * <p>
 * It is used as a message queue element.
 */
public final class HandlingEventRegistrationAttempt implements Serializable {

    private final Instant registrationTime;
    private final Instant completionTime;
    private final TrackingId trackingId;
    private final VoyageNumber voyageNumber;
    private final HandlingEvent.Type type;
    private final UnLocode unLocode;

    public HandlingEventRegistrationAttempt(@NonNull Instant registrationTime,
                                            @NonNull Instant completionTime,
                                            @NonNull TrackingId trackingId,
                                            @NonNull VoyageNumber voyageNumber,
                                            @NonNull HandlingEvent.Type type,
                                            @NonNull UnLocode unLocode) {
        this.registrationTime = registrationTime;
        this.completionTime = completionTime;
        this.trackingId = trackingId;
        this.voyageNumber = voyageNumber;
        this.type = type;
        this.unLocode = unLocode;
    }

    public Instant getCompletionTime() {
        return completionTime;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public VoyageNumber getVoyageNumber() {
        return voyageNumber;
    }

    public HandlingEvent.Type getType() {
        return type;
    }

    public UnLocode getUnLocode() {
        return unLocode;
    }

    public Instant getRegistrationTime() {
        return registrationTime;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
