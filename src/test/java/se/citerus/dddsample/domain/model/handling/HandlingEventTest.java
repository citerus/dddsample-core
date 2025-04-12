package se.citerus.dddsample.domain.model.handling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.*;

public class HandlingEventTest {
  private Cargo cargo;

  @BeforeEach
  public void setUp() {
    TrackingId trackingId = new TrackingId("XYZ");
    RouteSpecification routeSpecification = new RouteSpecification(HONGKONG, NEWYORK, Instant.now());
    cargo = new Cargo(trackingId, routeSpecification);
  }

  @Test
  public void testNewWithCarrierMovement() {

    HandlingEvent e1 = new HandlingEvent(cargo, Instant.now(), Instant.now(), LOAD, HONGKONG, CM003);
    assertThat(e1.location()).isEqualTo(HONGKONG);

    HandlingEvent e2 = new HandlingEvent(cargo, Instant.now(), Instant.now(), UNLOAD, NEWYORK, CM003);
    assertThat(e2.location()).isEqualTo(NEWYORK);

      // These event types prohibit a carrier movement association
    for (HandlingEvent.Type type : List.of(CLAIM, RECEIVE, CUSTOMS)) {
      try {
        new HandlingEvent(cargo, Instant.now(), Instant.now(), type, HONGKONG, CM003);
        fail("Handling event type " + type + " prohibits carrier movement");
      } catch (IllegalArgumentException expected) {}
    }

      // These event types requires a carrier movement association
    for (HandlingEvent.Type type : List.of(LOAD, UNLOAD)) {
        try {
          new HandlingEvent(cargo, Instant.now(), Instant.now(), type, HONGKONG, null);
            fail("Handling event type " + type + " requires carrier movement");
        } catch (NullPointerException expected) {}
    }
  }

  @Test
  public void testNewWithLocation() {
    HandlingEvent e1 = new HandlingEvent(cargo, Instant.now(), Instant.now(), HandlingEvent.Type.CLAIM, HELSINKI);
    assertThat(e1.location()).isEqualTo(HELSINKI);
  }

  @Test
  public void testCurrentLocationLoadEvent() {

    HandlingEvent ev = new HandlingEvent(cargo, Instant.now(), Instant.now(), LOAD, CHICAGO, CM004);
    
    assertThat(ev.location()).isEqualTo(CHICAGO);
  }

  @Test
  public void testCurrentLocationUnloadEvent() {
    HandlingEvent ev = new HandlingEvent(cargo, Instant.now(), Instant.now(), UNLOAD, HAMBURG, CM004);
    
    assertThat(ev.location()).isEqualTo(HAMBURG);
  }

  @Test
  public void testCurrentLocationReceivedEvent() {
    HandlingEvent ev = new HandlingEvent(cargo, Instant.now(), Instant.now(), RECEIVE, CHICAGO);

    assertThat(ev.location()).isEqualTo(CHICAGO);
  }

  @Test
  public void testCurrentLocationClaimedEvent() {
    HandlingEvent ev = new HandlingEvent(cargo, Instant.now(), Instant.now(), CLAIM, CHICAGO);

    assertThat(ev.location()).isEqualTo(CHICAGO);
  }

  @Test
  public void testParseType() {
    assertThat(valueOf("CLAIM")).isEqualTo(CLAIM);
    assertThat(valueOf("LOAD")).isEqualTo(LOAD);
    assertThat(valueOf("UNLOAD")).isEqualTo(UNLOAD);
    assertThat(valueOf("RECEIVE")).isEqualTo(RECEIVE);
  }

  @Test
  public void testParseTypeIllegal() {
    try {
      valueOf("NOT_A_HANDLING_EVENT_TYPE");
      fail("Expected IllegalArgumentException to be thrown");
    } catch (IllegalArgumentException e) {
      // All's well
    }
  }

  @Test
  public void testEqualsAndSameAs() {
    Instant timeOccured = Instant.now();
    Instant timeRegistered = Instant.now();

    HandlingEvent ev1 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, CM005);
    HandlingEvent ev2 = new HandlingEvent(cargo, timeOccured, timeRegistered, LOAD, CHICAGO, CM005);

    // Two handling events are not equal() even if all non-uuid fields are identical
    assertThat(ev1.equals(ev2)).isTrue();
    assertThat(ev2.equals(ev1)).isTrue();

    assertThat(ev1.equals(ev1)).isTrue();

    assertThat(ev2.equals(null)).isFalse();
    assertThat(ev2.equals(new Object())).isFalse();
  }
}
