package se.citerus.dddsample.interfaces.handling;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
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
@Getter
@RequiredArgsConstructor
public final class HandlingEventRegistrationAttempt implements Serializable {

    private final Instant registrationTime;
    private final Instant completionTime;
    private final TrackingId trackingId;
    private final VoyageNumber voyageNumber;
    private final HandlingEvent.Type type;
    private final UnLocode unLocode;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

}
