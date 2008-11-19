package se.citerus.dddsample.application.impl;

import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.application.SystemEvents;
import se.citerus.dddsample.application.messaging.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;

public final class HandlingEventServiceImpl implements HandlingEventService {

  private final HandlingEventRepository handlingEventRepository;
  private final SystemEvents systemEvents;
  private final HandlingEventFactory handlingEventFactory;

  public HandlingEventServiceImpl(HandlingEventRepository handlingEventRepository, SystemEvents systemEvents, HandlingEventFactory handlingEventFactory) {
    this.handlingEventRepository = handlingEventRepository;
    this.systemEvents = systemEvents;
    this.handlingEventFactory = handlingEventFactory;
  }

  /*
   NOTE:
     The cargo instance that's loaded and associated with the handling event is
     in an inconsitent state, because the cargo delivery history's collection of
     events does not contain the event created here. However, this is not a problem,
     because cargo is in a different aggregate from handling event.

     The rules of an aggregate dictate that all consistency rules within the aggregate
     are enforced synchronously in the transaction, but consistency rules of other aggregates
     are enforced by asynchronous updates, after the commit of this transaction.
  */
  @Override
  @Transactional
  public void register(HandlingEventRegistrationAttempt attempt) {
    try {
      final HandlingEvent event = handlingEventFactory.createHandlingEvent(attempt.getDate(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());
      handlingEventRepository.save(event);
      systemEvents.cargoWasHandled(event);
    } catch (CannotCreateHandlingEventException e) {
      systemEvents.rejectHandlingEventRegistrationAttempt(attempt, e);
    }
  }

}
