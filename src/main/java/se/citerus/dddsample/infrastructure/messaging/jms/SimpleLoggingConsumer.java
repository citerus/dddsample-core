package se.citerus.dddsample.infrastructure.messaging.jms;

import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class SimpleLoggingConsumer implements MessageListener {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void onMessage(Message message) {
    logger.debug("Received JMS message: {}", message);
  }

}
