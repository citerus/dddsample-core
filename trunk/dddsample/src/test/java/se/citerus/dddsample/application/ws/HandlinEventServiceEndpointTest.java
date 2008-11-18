package se.citerus.dddsample.application.ws;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import org.springframework.jms.core.JmsOperations;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Queue;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlinEventServiceEndpointTest extends TestCase {

  private HandlingEventServiceEndpointImpl endpoint;
  private SimpleDateFormat sdf = new SimpleDateFormat(HandlingEventServiceEndpointImpl.ISO_8601_FORMAT);
  private Date date = new Date(100);
  private JmsOperations jmsOperations;
  private Queue queue;

  protected void setUp() throws Exception {
    endpoint = new HandlingEventServiceEndpointImpl();

    jmsOperations = createMock(JmsOperations.class);
    queue = createMock(Queue.class);

    endpoint.setJmsOperations(jmsOperations);
    endpoint.setHandlingEventQueue(queue);
  }

  public void testRegisterValidEvent() throws Exception {
    jmsOperations.send(eq(queue), isA(MessageCreator.class));
    replay(jmsOperations, queue);

    // Tested call
    endpoint.register(sdf.format(date), "FOO", "CAR_456", "CNHKG", "LOAD");
  }

  public void testRegisterInalidEvent() throws Exception {
    replay(jmsOperations, queue);

    // Tested call
    try {
      endpoint.register("NOT_A_DATE", "", "", "NOT_A_UN_LOCODE", "NOT_A_TYPE");
      fail("Should not accept invalid ");
    } catch (Exception expected) {
      System.err.println(expected);
    }
  }

  protected void tearDown() throws Exception {
    verify(jmsOperations, queue);
  }
}
