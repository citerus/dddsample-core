package se.citerus.dddsample.infrastructure.messaging.jms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.Message;
import javax.jms.MessageListener;

public class SimpleLoggingConsumer implements MessageListener {

  private final Log logger = LogFactory.getLog(SimpleLoggingConsumer.class);

  @Override
  public void onMessage(Message message) {
    logger.debug("Received JMS message: " + message);
  }

}
