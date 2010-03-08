package se.citerus.dddsample.tracking.core.domain.model.handling;

import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType;
import se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages;

import java.util.Date;

import static java.util.Arrays.asList;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.shared.HandlingActivityType.*;

public class HandlingEventTest extends TestCase {
  private Cargo cargo;

  protected void setUp() throws Exception {
    TrackingId trackingId = new TrackingId("XYZ");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, NEWYORK, new Date());
    cargo = new Cargo(trackingId, routeSpecification);
  }

  public void testNewWithCarrierMovement() throws Exception {

    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), LOAD, HONGKONG, SampleVoyages.continental1, new OperatorCode("ABCDE"));
    assertEquals(HONGKONG, e1.location());

    HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, NEWYORK, SampleVoyages.continental1, new OperatorCode("ABCDE"));
    assertEquals(NEWYORK, e2.location());

    // These event types prohibit a carrier movement association
    for (HandlingActivityType type : asList(CLAIM, RECEIVE, CUSTOMS)) {
      try {
        new HandlingEvent(cargo, new Date(), new Date(), type, HONGKONG, SampleVoyages.continental1, new OperatorCode("ABCDE"));
        fail("Handling event type " + type + " prohibits carrier movement");
      } catch (IllegalArgumentException expected) {
      }
    }

    // These event types requires a carrier movement association
    for (HandlingActivityType type : asList(LOAD, UNLOAD)) {
      try {
        new HandlingEvent(cargo, new Date(), new Date(), type, HONGKONG, null, new OperatorCode("ABCDE"));
        fail("Handling event type " + type + " requires carrier movement");
      } catch (IllegalArgumentException expected) {
      }
    }
  }

  public void testNewWithLocation() throws Exception {
    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), HandlingActivityType.CLAIM, HELSINKI);
    assertEquals(HELSINKI, e1.location());
  }

  public void testCurrentLocationLoadEvent() throws Exception {

    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), LOAD, CHICAGO, SampleVoyages.continental2, new OperatorCode("ABCDE"));

    assertEquals(CHICAGO, ev.location());
  }

  public void testCurrentLocationUnloadEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, HAMBURG, SampleVoyages.continental2, new OperatorCode("ABCDE"));

    assertEquals(HAMBURG, ev.location());
  }

  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), RECEIVE, CHICAGO);

    assertEquals(CHICAGO, ev.location());
  }

  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), CLAIM, CHICAGO);

    assertEquals(CHICAGO, ev.location());
  }

  public void testParseType() throws Exception {
    assertEquals(CLAIM, valueOf("CLAIM"));
    assertEquals(LOAD, valueOf("LOAD"));
    assertEquals(UNLOAD, valueOf("UNLOAD"));
    assertEquals(RECEIVE, valueOf("RECEIVE"));
  }

  public void testParseTypeIllegal() throws Exception {
    try {
      valueOf("NOT_A_HANDLING_EVENT_TYPE");
      assertTrue("Expected IllegaArgumentException to be thrown", false);
    } catch (IllegalArgumentException e) {
      // All's well
    }
  }

  public void testEqualsAndSameAs() throws Exception {
    Date timeOccured = new Date();
    Date timeRegistered = new Date();

    HandlingEvent ev1 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, SampleVoyages.atlantic1, new OperatorCode("ABCDE"));
    HandlingEvent ev2 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, SampleVoyages.atlantic1, new OperatorCode("ABCDE"));

    assertTrue(ev1.equals(ev2));
    assertTrue(ev2.equals(ev1));

    assertTrue(ev1.equals(ev1));

    //noinspection ObjectEqualsNull
    assertFalse(ev2.equals(null));

    assertFalse(ev2.equals(new Object()));
  }

}
