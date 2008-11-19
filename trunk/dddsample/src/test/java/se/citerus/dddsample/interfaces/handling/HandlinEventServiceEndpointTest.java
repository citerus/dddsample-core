package se.citerus.dddsample.interfaces.handling;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.application.SystemEvents;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HandlinEventServiceEndpointTest extends TestCase {

  private HandlingEventServiceEndpointImpl endpoint;
  private SimpleDateFormat sdf = new SimpleDateFormat(HandlingEventServiceEndpointImpl.ISO_8601_FORMAT);
  private Date date = new Date(100);
  private SystemEvents systemEvents;

  protected void setUp() throws Exception {
    endpoint = new HandlingEventServiceEndpointImpl();

    systemEvents = createMock(SystemEvents.class);
    endpoint.setSystemEvents(systemEvents);
  }

  public void testRegisterValidEvent() throws Exception {
    systemEvents.receivedHandlingEventRegistrationAttempt(isA(HandlingEventRegistrationAttempt.class));
    replay(systemEvents);

    // Tested call
    endpoint.register(sdf.format(date), "FOO", "CAR_456", "CNHKG", "LOAD");
  }

  public void testRegisterInalidEvent() throws Exception {
    replay(systemEvents);

    // Tested call
    try {
      endpoint.register("NOT_A_DATE", "", "", "NOT_A_UN_LOCODE", "NOT_A_TYPE");
      fail("Should not accept invalid ");
    } catch (Exception expected) {
      System.err.println(expected);
    }
  }

  protected void tearDown() throws Exception {
    verify(systemEvents);
  }
}
