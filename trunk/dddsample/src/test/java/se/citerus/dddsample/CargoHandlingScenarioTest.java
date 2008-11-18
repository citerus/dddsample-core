package se.citerus.dddsample;

import junit.framework.TestCase;
import se.citerus.dddsample.application.persistence.LocationRepositoryInMem;
import se.citerus.dddsample.application.persistence.VoyageRepositoryInMem;
import se.citerus.dddsample.domain.model.cargo.*;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.*;
import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.service.DomainEventNotifier;
import se.citerus.dddsample.domain.service.HandlingEventService;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.domain.service.TrackingService;
import se.citerus.dddsample.domain.service.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.service.impl.TrackingServiceImpl;

import java.util.*;

/**
 * Cargo scenarios.
 */
public class CargoHandlingScenarioTest extends TestCase {

  HandlingEventFactory handlingEventFactory;

  DomainEventNotifier domainEventNotifier;

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
    HandlingEvent received = handlingEventFactory.createHandlingEvent(
      dateReceived(), trackingId, null, HONGKONG.unLocode(), HandlingEvent.Type.RECEIVE);

    handlingEventService.register(received);


    // Loaded onto carrier movement CM003 in Hongkong
    HandlingEvent loadedInHongkong = handlingEventFactory.createHandlingEvent(
      dateLoadingCompleted(), trackingId, CM003.voyageNumber(), HONGKONG.unLocode(), HandlingEvent.Type.LOAD);

    handlingEventService.register(loadedInHongkong);

    // Check current state - should be ok
    assertEquals(CM003, cargo.delivery().currentVoyage());
    assertEquals(HONGKONG, cargo.delivery().lastKnownLocation());
    assertEquals(TransportStatus.ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());


    // Cargo is now (incorrectly) unloaded in Tokyo
    HandlingEvent unloadedInTokyo = handlingEventFactory.createHandlingEvent(
      dateUnloadingCompleted(), trackingId, CM003.voyageNumber(), TOKYO.unLocode(), HandlingEvent.Type.UNLOAD);
    handlingEventService.register(unloadedInTokyo);

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
    domainEventNotifier = new DomainEventNotifier() {
      public void cargoWasHandled(HandlingEvent event) {
        trackingService.onCargoHandled(event.cargo().trackingId());
      }
      public void cargoWasMisdirected(Cargo cargo) {}
      public void cargoHasArrived(Cargo cargo) {}
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
    trackingService = new TrackingServiceImpl(domainEventNotifier, cargoRepository);
    handlingEventFactory = new HandlingEventFactory(cargoRepository, this.voyageRepository, this.locationRepository);
    handlingEventService = new HandlingEventServiceImpl(handlingEventRepository, domainEventNotifier);
  }
}
