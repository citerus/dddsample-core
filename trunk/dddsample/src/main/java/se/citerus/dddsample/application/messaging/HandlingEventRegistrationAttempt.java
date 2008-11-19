package se.citerus.dddsample.application.messaging;

import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.io.Serializable;
import java.util.Date;

/**
 * This is a simple data holder for passing incoming handling event
 * registration attempts to proper the registration procedure.
 *  
 */
public class HandlingEventRegistrationAttempt implements Serializable {

  private final Date registrationDate;
  private final Date date;
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
    this.registrationDate = registrationDate;
    this.date = completionDate;
    this.trackingId = trackingId;
    this.voyageNumber = voyageNumber;
    this.type = type;
    this.unLocode = unLocode;
  }

  public Date getDate() {
    return date;
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

  public Date getRegistrationDate() {
    return registrationDate;
  }
}
