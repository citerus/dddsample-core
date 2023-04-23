package se.citerus.dddsample.infrastructure.messaging.jms;

import se.citerus.dddsample.logging.Logger;
import se.citerus.dddsample.logging.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.lang.invoke.MethodHandles;

public class SimpleLoggingConsumer implements MessageListener {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void onMessage(Message message) {
    logger.debug("Received JMS message: {}", fb -> fb.apply(message));
  }

}
