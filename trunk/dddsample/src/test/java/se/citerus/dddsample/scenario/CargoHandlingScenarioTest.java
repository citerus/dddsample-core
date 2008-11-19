package se.citerus.dddsample.scenario;

import junit.framework.TestCase;
import se.citerus.dddsample.application.HandlingEventRegistrationAttempt;
import se.citerus.dddsample.application.HandlingEventService;
import se.citerus.dddsample.application.SystemEvents;
import se.citerus.dddsample.application.TrackingService;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.application.impl.TrackingServiceImpl;
import se.citerus.dddsample.domain.model.cargo.*;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.*;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.handling.CannotCreateHandlingEventException;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.util.*;

/**
 * Cargo scenarios.
 */
public class CargoHandlingScenarioTest extends TestCase {

  HandlingEventFactory handlingEventFactory;

  SystemEvents systemEvents;

  HandlingEventService handlingEventService;
  TrackingService trackingService;
  RoutingService routingService;

  HandlingEventRepository handlingEventRepository;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  VoyageRepository voyageRepository;

  Cargo cargo;


  public void testCargoFromHongkongToStockholm() throws Exception {

    // Cargo from Hongkong to Stockholm (skipping the booking procedure here)
    TrackingId trackingId = generateTrackingId();
    Location origin = HONGKONG;
    Location destination = STOCKHOLM;
    cargo = new Cargo(trackingId, origin, destination);


    assertEquals(RoutingStatus.NOT_ROUTED, cargo.routingStatus());

    // Route cargo
    RouteSpecification routeSpecification = new RouteSpecification(cargo.origin(), cargo.destination(), inTwoWeeks());
    List<Itinerary> itineraries = routingService.fetchRoutesForSpecification(routeSpecification);
    Itinerary itinerary = selectPreferedItinerary(itineraries);
    cargo.assignToRoute(itinerary);

    // Handling begins: cargo is received in Hongkong
    handlingEventService.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, null, HandlingEvent.Type.RECEIVE, HONGKONG.unLocode()
    ));


    // Loaded onto carrier movement CM003 in Hongkong
    handlingEventService.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, CM003.voyageNumber(), HandlingEvent.Type.LOAD, HONGKONG.unLocode()
    ));

    // Check current state - should be ok
    assertEquals(CM003, cargo.delivery().currentVoyage());
    assertEquals(HONGKONG, cargo.delivery().lastKnownLocation());
    assertEquals(TransportStatus.ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());


    // Cargo is now (incorrectly) unloaded in Tokyo
    handlingEventService.register(new HandlingEventRegistrationAttempt(
      new Date(), new Date(), trackingId, CM003.voyageNumber(), HandlingEvent.Type.UNLOAD, TOKYO.unLocode()
    ));

    // Check current state - cargo is misdirected!
    assertEquals(Voyage.NONE, cargo.delivery().currentVoyage());
    assertEquals(TOKYO, cargo.delivery().lastKnownLocation());
    assertEquals(TransportStatus.IN_PORT, cargo.delivery().transportStatus());
    assertTrue(cargo.isMisdirected());

    // Then: Reroute!
  }

  

  /*
   * Utility stubs below.
   */

  private Date dateReceived() {
    return new Date(100);
  }

  private Date dateLoadingCompleted() {
    return new Date(200);
  }

  private Date dateUnloadingCompleted() {
    return new Date(300);
  }

  private TrackingId generateTrackingId() {
    return new TrackingId("TI1");
  }

  private Date inTwoWeeks() {
    return new Date(new Date().getTime() + 1000*60*60*24*14);
  }

  private Itinerary selectPreferedItinerary(List<Itinerary> itineraries) {
    return itineraries.get(0);
  }

  protected void setUp() throws Exception {

    // Stub
    routingService = new RoutingService() {
      public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
        return Arrays.asList(
          new Itinerary(Arrays.asList(
                new Leg(CM003, HONGKONG, NEWYORK, new Date(), new Date()),
                new Leg(CM004, NEWYORK, CHICAGO, new Date(), new Date()),
                new Leg(CM005, CHICAGO, STOCKHOLM, new Date(), new Date())
          ))
        );
      }
    };

    // Synchronous stub
    systemEvents = new SystemEvents() {
      @Override
      public void cargoWasHandled(HandlingEvent event) {
        trackingService.onCargoHandled(event.cargo().trackingId());
      }
      @Override
      public void cargoWasMisdirected(Cargo cargo) {}
      @Override
      public void cargoHasArrived(Cargo cargo) {}
      @Override
      public void rejectHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt, CannotCreateHandlingEventException problem) {}
      @Override
      public void receivedHandlingEventRegistrationAttempt(HandlingEventRegistrationAttempt attempt) {}
    };

    // Stub
    cargoRepository = new CargoRepository() {
      public Cargo find(TrackingId trackingId) {
        return cargo;
      }

      public List<Cargo> findAll() {
        return null;
      }

      public void save(Cargo cargo) {
      }

      public TrackingId nextTrackingId() {
        return null;
      }
    };

    // Stub
    handlingEventRepository = new HandlingEventRepository() {
      public void save(HandlingEvent event) {
        Set<HandlingEvent> events = new HashSet<HandlingEvent>(cargo.delivery().history());
        events.add(event);
        CargoTestHelper.setDeliveryHistory(cargo, events);
      }

      public List<HandlingEvent> findEventsForCargo(TrackingId trackingId) {
        return null;
      }
    };

    // In-memory implementations
    locationRepository = new LocationRepositoryInMem();
    voyageRepository = new VoyageRepositoryInMem();

    // Domain services and factories that are implemented in the domain layer - not stubbed
    trackingService = new TrackingServiceImpl(systemEvents, cargoRepository);
    handlingEventFactory = new HandlingEventFactory(cargoRepository, this.voyageRepository, this.locationRepository);
    handlingEventService = new HandlingEventServiceImpl(handlingEventRepository, systemEvents, handlingEventFactory);
  }
}
