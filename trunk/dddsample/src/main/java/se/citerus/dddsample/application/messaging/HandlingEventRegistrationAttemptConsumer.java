package se.citerus.dddsample.application.messaging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.HandlingEventService;
import se.citerus.dddsample.domain.service.UnknownCargoException;
import se.citerus.dddsample.domain.service.UnknownLocationException;
import se.citerus.dddsample.domain.service.UnknownVoyageException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Date;

/**
 */
public class HandlingEventRegistrationAttemptConsumer implements MessageListener {

  private HandlingEventFactory handlingEventFactory;
  private HandlingEventService handlingEventService;
  private static final Log logger = LogFactory.getLog(HandlingEventRegistrationAttemptConsumer.class);

  @Transactional(readOnly = false)
  public void onMessage(Message message) {
    try {
      ObjectMessage om = (ObjectMessage) message;
      HandlingEventRegistrationAttempt attempt = (HandlingEventRegistrationAttempt) om.getObject();
      doRegister(attempt.getDate(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getType(), attempt.getUnLocode());
    } catch (JMSException e) {
      logger.error(e, e);
    }
  }

  private void doRegister(final Date date, final TrackingId trackingId, final VoyageNumber voyageNumber, final HandlingEvent.Type type, final UnLocode unLocode) {
    try {
        HandlingEvent event = handlingEventFactory.createHandlingEvent(date, trackingId, voyageNumber, unLocode, type);
        handlingEventService.register(event);
    } catch (UnknownVoyageException e) {
      handleUnknownCarrierMovementId(e);
    } catch (UnknownCargoException e) {
      handleUnknownTrackingId(e);
    } catch (UnknownLocationException e) {
      handleUnknownLocation(e);
    }
  }

  private void handleUnknownLocation(UnknownLocationException e) {
    logger.error(e, e);
  }

  private void handleUnknownCarrierMovementId(UnknownVoyageException e) {
    logger.error(e, e);
  }

  private void handleUnknownTrackingId(Exception e) {
    logger.error(e, e);
  }

  // Setters

  public void setHandlingEventService(HandlingEventService handlingEventService) {
    this.handlingEventService = handlingEventService;
  }

  public void setHandlingEventFactory(HandlingEventFactory handlingEventFactory) {
    this.handlingEventFactory = handlingEventFactory;
  }

}
