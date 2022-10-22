package se.citerus.dddsample.infrastructure.messaging.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.lang.invoke.MethodHandles;

public class SimpleLoggingConsumer implements MessageListener {

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Override
  public void onMessage(Message message) {
    logger.debug("Received JMS message: {}", message);
  }

}
