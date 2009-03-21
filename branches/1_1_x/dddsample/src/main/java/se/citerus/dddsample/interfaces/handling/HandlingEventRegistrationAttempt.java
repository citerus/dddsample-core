package se.citerus.dddsample.interfaces.handling;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a simple transfer object for passing incoming handling event
 * registration attempts to proper the registration procedure.
 *
 * It is used as a message queue element. 
 *
 */
public final class HandlingEventRegistrationAttempt implements Serializable {

  private final Date registrationTime;
  private final Date completionTime;
  private final TrackingId trackingId;
  private final VoyageNumber voyageNumber;
  private final HandlingEvent.Type type;
  private final UnLocode unLocode;

  public HandlingEventRegistrationAttempt(final Date registrationDate,
                                          final Date completionDate,
                                          final TrackingId trackingId,
                                          final VoyageNumber voyageNumber,
                                          final HandlingEvent.Type type,
                                          final UnLocode unLocode) {
    this.registrationTime = registrationDate;
    this.completionTime = completionDate;
    this.trackingId = trackingId;
    this.voyageNumber = voyageNumber;
    this.type = type;
    this.unLocode = unLocode;
  }

  public Date getCompletionTime() {
    return new Date(completionTime.getTime());
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

  public Date getRegistrationTime() {
    return registrationTime;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
  
}
