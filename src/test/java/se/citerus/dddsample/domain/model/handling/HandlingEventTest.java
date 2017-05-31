package se.citerus.dddsample.domain.model.handling;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.voyage.SampleVoyages;

import java.util.Date;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM003;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM004;

public class HandlingEventTest extends TestCase {
  private Cargo cargo;

  protected void setUp() throws Exception {
    TrackingId trackingId = new TrackingId("XYZ");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, NEWYORK, new Date());
    cargo = new Cargo(trackingId, routeSpecification);
  }

  public void testNewWithCarrierMovement() throws Exception {

    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), LOAD, HONGKONG, CM003);
    assertThat(e1.location()).isEqualTo(HONGKONG);

    HandlingEvent e2 = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, NEWYORK, CM003);
    assertThat(e2.location()).isEqualTo(NEWYORK);

      // These event types prohibit a carrier movement association
    for (HandlingEvent.Type type : asList(CLAIM, RECEIVE, CUSTOMS)) {
      try {
        new HandlingEvent(cargo, new Date(), new Date(), type, HONGKONG, CM003);
        fail("Handling event type " + type + " prohibits carrier movement");
      } catch (IllegalArgumentException expected) {}
    }

      // These event types requires a carrier movement association
    for (HandlingEvent.Type type : asList(LOAD, UNLOAD)) {
        try {
          new HandlingEvent(cargo, new Date(), new Date(), type, HONGKONG, null);
            fail("Handling event type " + type + " requires carrier movement");
        } catch (IllegalArgumentException expected) {}
    }
  }

  public void testNewWithLocation() throws Exception {
    HandlingEvent e1 = new HandlingEvent(cargo, new Date(), new Date(), HandlingEvent.Type.CLAIM, HELSINKI);
    assertThat(e1.location()).isEqualTo(HELSINKI);
  }

  public void testCurrentLocationLoadEvent() throws Exception {

    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), LOAD, CHICAGO, CM004);
    
    assertThat(ev.location()).isEqualTo(CHICAGO);
  }
  
  public void testCurrentLocationUnloadEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), UNLOAD, HAMBURG, CM004);
    
    assertThat(ev.location()).isEqualTo(HAMBURG);
  }
  
  public void testCurrentLocationReceivedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), RECEIVE, CHICAGO);

    assertThat(ev.location()).isEqualTo(CHICAGO);
  }
  public void testCurrentLocationClaimedEvent() throws Exception {
    HandlingEvent ev = new HandlingEvent(cargo, new Date(), new Date(), CLAIM, CHICAGO);

    assertThat(ev.location()).isEqualTo(CHICAGO);
  }
  
  public void testParseType() throws Exception {
    assertThat(valueOf("CLAIM")).isEqualTo(CLAIM);
    assertThat(valueOf("LOAD")).isEqualTo(LOAD);
    assertThat(valueOf("UNLOAD")).isEqualTo(UNLOAD);
    assertThat(valueOf("RECEIVE")).isEqualTo(RECEIVE);
  }
  
  public void testParseTypeIllegal() throws Exception {
    try {
      valueOf("NOT_A_HANDLING_EVENT_TYPE");
      assertThat(false).as("Expected IllegaArgumentException to be thrown").isTrue();
    } catch (IllegalArgumentException e) {
      // All's well
    }
  }
  
  public void testEqualsAndSameAs() throws Exception {
    Date timeOccured = new Date();
    Date timeRegistered = new Date();

    HandlingEvent ev1 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, SampleVoyages.CM005);
    HandlingEvent ev2 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, SampleVoyages.CM005);

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertThat(ev1.equals(ev2)).isTrue();
    assertThat(ev2.equals(ev1)).isTrue();

    assertThat(ev1.equals(ev1)).isTrue();

    assertThat(ev2.equals(null)).isFalse();
    assertThat(ev2.equals(new Object())).isFalse();
  }

}
