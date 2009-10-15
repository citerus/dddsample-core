package se.citerus.dddsample.tracking.core.application.handling;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.tracking.core.application.event.SystemEvents;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;

import java.util.Date;

@Service
public final class HandlingEventServiceImpl implements HandlingEventService {

  private final SystemEvents systemEvents;
  private final HandlingEventRepository handlingEventRepository;
  private final HandlingEventFactory handlingEventFactory;
  private final Log logger = LogFactory.getLog(HandlingEventServiceImpl.class);

  @Autowired
  public HandlingEventServiceImpl(final HandlingEventRepository handlingEventRepository,
                                  final SystemEvents systemEvents,
                                  final HandlingEventFactory handlingEventFactory) {
    this.handlingEventRepository = handlingEventRepository;
    this.systemEvents = systemEvents;
    this.handlingEventFactory = handlingEventFactory;
  }

  @Override
  @Transactional(rollbackFor = CannotCreateHandlingEventException.class)
  public void registerHandlingEvent(final Date completionTime, final TrackingId trackingId,
                                    final VoyageNumber voyageNumber, final UnLocode unLocode,
                                    final HandlingEvent.Type type) throws CannotCreateHandlingEventException {

    /* Using a factory to create a HandlingEvent (aggregate). This is where
       it is determined wether the incoming data, the attempt, actually is capable
       of representing a real handling event. */
    final HandlingEvent event = handlingEventFactory.createHandlingEvent(
      completionTime, trackingId, voyageNumber, unLocode, type
    );

    /* Store the new handling event, which updates the persistent
       state of the handling event aggregate (but not the cargo aggregate -
       that happens asynchronously!)
     */
    handlingEventRepository.store(event);

    /* Publish an event stating that a cargo has been handled. */
    systemEvents.notifyOfHandlingEvent(event);

    logger.info("Registered handling event");
  }

}
