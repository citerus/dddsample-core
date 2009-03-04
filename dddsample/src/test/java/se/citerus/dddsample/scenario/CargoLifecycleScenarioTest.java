package se.citerus.dddsample.scenario;

import junit.framework.TestCase;
import se.citerus.dddsample.application.*;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.application.impl.CargoInspectionServiceImpl;
import se.citerus.dddsample.application.impl.HandlingEventServiceImpl;
import se.citerus.dddsample.domain.model.cargo.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.*;
import static se.citerus.dddsample.domain.model.carrier.Voyage.NONE;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.domain.model.handling.HandlingEventFactory;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.infrastructure.messaging.stub.SynchronousApplicationEventsStub;
import se.citerus.dddsample.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.util.*;

public class CargoLifecycleScenarioTest extends TestCase {

  /**
   * Repository implementations are part of the infrastructure layer,
   * which in this test is stubbed out by in-memory replacements.
   */
  HandlingEventRepository handlingEventRepository;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  VoyageRepository voyageRepository;

  /**
   * This interface is part of the application layer,
   * and defines a number of events that occur during
   * aplication execution. It is used for message-driving
   * and is implemented using JMS.
   *
   * In this test it is stubbed with synchronous calls.
   */
  ApplicationEvents applicationEvents;

  /**
   * These three components all belong to the application layer,
   * and map against use cases of the application. The "real"
   * implementations are used in this lifecycle test,
   * but wired with stubbed infrastructure.
   */
  BookingService bookingService;
  HandlingEventService handlingEventService;
  CargoInspectionService cargoInspectionService;

  /**
   * This factory is part of the handling aggregate and belongs to
   * the domain layer. Similar to the application layer components,
   * the "real" implementation is used here too,
   * wired with stubbed infrastructure.
   */
  HandlingEventFactory handlingEventFactory;

  /**
   * This is a domain service interface, whose implementation
   * is part of the infrastructure layer (remote call to external system).
   *
   * It is stubbed in this test.
   */
  RoutingService routingService;


  public void testCargoFromHongkongToStockholm() throws Exception {
    /* Test setup: A cargo should be shipped from Hongkong to Stockholm,
       and it should arrive in no more than two weeks. */
    Location origin = HONGKONG;
    Location destination = STOCKHOLM;
    Date arrivalDeadline = inTwoWeeks();

    /* Use case 1: booking

       A new cargo is booked, and the unique tracking id is assigned to the cargo. */
    TrackingId trackingId = bookingService.bookNewCargo(
      origin.unLocode(), destination.unLocode(), arrivalDeadline
    );

    /* The tracking id can be used to lookup the cargo in the repository.

       Important: The cargo, and thus the domain model, is responsible for determining
       the status of the cargo, whether it is on the right track or not and so on.
       This is core domain logic.

       Tracking the cargo basically amounts to presenting information extracted from
       the cargo aggregate in a suitable way. */
    Cargo cargo = cargoRepository.find(trackingId);
    assertNotNull(cargo);
    assertEquals(NOT_RECEIVED, cargo.delivery().transportStatus());
    assertEquals(RoutingStatus.NOT_ROUTED, cargo.routingStatus());
    assertFalse(cargo.isMisdirected());


    /* Use case 2: routing

       A number of possible routes for this cargo is requested and may be
       presented to the customer in some way for him/her to choose from.
       Selection could be affected by things like price and time of delivery,
       but this test simply uses an arbitrary selection to mimic that process.

       The cargo is then assigned to the selected route, described by an itinerary. */
    List<Itinerary> itineraries = bookingService.requestPossibleRoutesForCargo(trackingId);
    Itinerary itinerary = selectPreferedItinerary(itineraries);
    cargo.assignToRoute(itinerary);

    assertEquals(RoutingStatus.ROUTED, cargo.routingStatus());

    /*
      Use case 3: handling

      A handling event registration attempt will be formed from parsing
      the data coming in as a handling report either via
      the web service interface or as an uploaded CSV file.

      The handling event factory tries to create a HandlingEvent from the attempt,
      and if the factory decides that this is a plausible handling event, it is stored.
      If the attempt is invalid, for example if no cargo exists for the specfied tracking id,
      the attempt is rejected.

      Handling begins: cargo is received in Hongkong.
      */
    HandlingEventRegistrationAttempt attempt1 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(100), trackingId, null, RECEIVE, HONGKONG.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt1.getCompletionTime(), attempt1.getTrackingId(), attempt1.getVoyageNumber(), attempt1.getUnLocode(), attempt1.getType());

    // Next event: Load onto voyage CM003 in Hongkong
    final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(200), trackingId, CM003.voyageNumber(), LOAD, HONGKONG.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt.getCompletionTime(), attempt.getTrackingId(), attempt.getVoyageNumber(), attempt.getUnLocode(), attempt.getType());

    // Check current state - should be ok
    assertEquals(CM003, cargo.delivery().currentVoyage());
    assertEquals(HONGKONG, cargo.delivery().lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());

    /*
      Here's an attempt to register a handling event that's not valid
      because there is no voyage with the specified voyage number,
      and there's no location with the specified UN Locode either.

      This attempt will be rejected and will not affet the cargo delivery in any way.
     */
    VoyageNumber noSuchVoyageNumber = new VoyageNumber("XX000");
    UnLocode noSuchUnLocode = new UnLocode("ZZZZZ");
    HandlingEventRegistrationAttempt failingAttempt = new HandlingEventRegistrationAttempt(
      new Date(), new Date(300), trackingId, noSuchVoyageNumber, LOAD, noSuchUnLocode
    );
    handlingEventService.registerHandlingEvent(failingAttempt.getCompletionTime(), failingAttempt.getTrackingId(), failingAttempt.getVoyageNumber(), failingAttempt.getUnLocode(), failingAttempt.getType());


    // Cargo is now (incorrectly) unloaded in Tokyo
    final HandlingEventRegistrationAttempt attempt7 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(400), trackingId, CM003.voyageNumber(), UNLOAD, TOKYO.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt7.getCompletionTime(), attempt7.getTrackingId(), attempt7.getVoyageNumber(), attempt7.getUnLocode(), attempt7.getType());

    // Check current state - cargo is misdirected!
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(TOKYO, cargo.delivery().lastKnownLocation());
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertTrue(cargo.isMisdirected());


    // -- Cargo needs to be rerouted --


    // Specify a new route, this time from Tokyo (where it was incorrectly unloaded) to Stockholm
    RouteSpecification fromTokyo = new RouteSpecification(TOKYO, STOCKHOLM, arrivalDeadline);
    cargo.specifyNewRoute(fromTokyo);

    // The old itinerary does not satisfy the new specification
    assertEquals(RoutingStatus.MISROUTED, cargo.routingStatus());

    // Repeat procedure of selecting one out of a number of possible routes satisfying the route spec
    List<Itinerary> newItineraries = bookingService.requestPossibleRoutesForCargo(cargo.trackingId());
    Itinerary newItinerary = selectPreferedItinerary(newItineraries);
    cargo.assignToRoute(newItinerary);

    // New itinerary should satisfy new route
    assertEquals(RoutingStatus.ROUTED, cargo.routingStatus());


    // -- Cargo has been rerouted, shipping continues --


    // Load in Tokyo
    HandlingEventRegistrationAttempt attempt2 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(500), trackingId, CM003.voyageNumber(), LOAD, TOKYO.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt2.getCompletionTime(), attempt2.getTrackingId(), attempt2.getVoyageNumber(), attempt2.getUnLocode(), attempt2.getType());

    // Check current state - should be ok
    assertEquals(CM003, cargo.delivery().currentVoyage());
    assertEquals(TOKYO, cargo.delivery().lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());

    // Unload in Hamburg
    HandlingEventRegistrationAttempt attempt3 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(600), trackingId, CM003.voyageNumber(), UNLOAD, HAMBURG.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt3.getCompletionTime(), attempt3.getTrackingId(), attempt3.getVoyageNumber(), attempt3.getUnLocode(), attempt3.getType());

    // Check current state - should be ok
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(HAMBURG, cargo.delivery().lastKnownLocation());
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());

    // Load in Hamburg
    HandlingEventRegistrationAttempt attempt4 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(700), trackingId, CM005.voyageNumber(), LOAD, HAMBURG.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt4.getCompletionTime(), attempt4.getTrackingId(), attempt4.getVoyageNumber(), attempt4.getUnLocode(), attempt4.getType());

    // Check current state - should be ok
    assertEquals(CM005, cargo.delivery().currentVoyage());
    assertEquals(HAMBURG, cargo.delivery().lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());

    // Unload in Stockholm
    HandlingEventRegistrationAttempt attempt5 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(800), trackingId, CM005.voyageNumber(), UNLOAD, STOCKHOLM.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt5.getCompletionTime(), attempt5.getTrackingId(), attempt5.getVoyageNumber(), attempt5.getUnLocode(), attempt5.getType());

    // Check current state - should be ok
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(STOCKHOLM, cargo.delivery().lastKnownLocation());
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());

    // Finally, cargo is claimed in Stockholm. This ends the cargo lifecycle from our perspective.
    HandlingEventRegistrationAttempt attempt6 = new HandlingEventRegistrationAttempt(
      new Date(), new Date(900), trackingId, null, CLAIM, STOCKHOLM.unLocode()
    );
    handlingEventService.registerHandlingEvent(attempt6.getCompletionTime(), attempt6.getTrackingId(), attempt6.getVoyageNumber(), attempt6.getUnLocode(), attempt6.getType());

    // Check current state - should be ok
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(STOCKHOLM, cargo.delivery().lastKnownLocation());
    assertEquals(CLAIMED, cargo.delivery().transportStatus());
    assertFalse(cargo.isMisdirected());
  }

  /*
  * Utility stubs below.
  */

  private Date inTwoWeeks() {
    return new Date(new Date().getTime() + 1000 * 60 * 60 * 24 * 14);
  }

  private Itinerary selectPreferedItinerary(List<Itinerary> itineraries) {
    return itineraries.get(0);
  }

  protected void setUp() throws Exception {
    // Stub
    // TODO new voyages
    routingService = new RoutingService() {
      public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
        if (routeSpecification.origin().equals(HONGKONG)) {
          // Hongkong - NYC - Chicago - Stockholm, initial routing
          return Arrays.asList(
            new Itinerary(Arrays.asList(
              new Leg(CM003, HONGKONG, NEWYORK, new Date(), new Date()),
              new Leg(CM004, NEWYORK, CHICAGO, new Date(), new Date()),
              new Leg(CM005, CHICAGO, STOCKHOLM, new Date(), new Date())
            ))
          );
        } else {
          // Tokyo - Hamburg - Stockholm, rerouting
          return Arrays.asList(
            new Itinerary(Arrays.asList(
              new Leg(CM003, TOKYO, HAMBURG, new Date(), new Date()),
              new Leg(CM005, HAMBURG, STOCKHOLM, new Date(), new Date())
            ))
          );
        }
      }
    };


    applicationEvents = new SynchronousApplicationEventsStub();

    // Stub
    // TODO move functionality to in-mem impl
    handlingEventRepository = new HandlingEventRepository() {
      Map<TrackingId, List<HandlingEvent>> eventMap = new HashMap<TrackingId, List<HandlingEvent>>();

      public void store(HandlingEvent event) {
        final TrackingId trackingId = event.cargo().trackingId();
        List<HandlingEvent> list = eventMap.get(trackingId);
        if (list == null) {
          list = new ArrayList<HandlingEvent>();
          eventMap.put(trackingId, list);
        }
        list.add(event);
      }

      public List<HandlingEvent> findEventsForCargo(TrackingId trackingId) {
        return eventMap.get(trackingId);
      }
    };

    // In-memory implementations
    cargoRepository = new CargoRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    voyageRepository = new VoyageRepositoryInMem();

    // Actual factories and application services, wired with stubbed or in-memory infrastructure
    cargoInspectionService = new CargoInspectionServiceImpl(applicationEvents, cargoRepository, handlingEventRepository);
    handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
    handlingEventService = new HandlingEventServiceImpl(handlingEventRepository, applicationEvents, handlingEventFactory);
    bookingService = new BookingServiceImpl(cargoRepository, locationRepository, routingService);

    // Stub
    ((SynchronousApplicationEventsStub) applicationEvents).setCargoInspectionService(cargoInspectionService);
  }

}
