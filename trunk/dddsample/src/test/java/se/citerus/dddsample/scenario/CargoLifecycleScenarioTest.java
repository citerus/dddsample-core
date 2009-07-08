package se.citerus.dddsample.scenario;

import static org.junit.Assert.*;
import org.junit.Test;
import static se.citerus.dddsample.application.util.DateTestUtil.toDate;
import se.citerus.dddsample.domain.model.cargo.*;
import static se.citerus.dddsample.domain.model.cargo.RoutingStatus.*;
import static se.citerus.dddsample.domain.model.cargo.TransportStatus.*;
import se.citerus.dddsample.domain.model.handling.*;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.*;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import static se.citerus.dddsample.domain.model.voyage.Voyage.NONE;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.domain.service.RoutingService;
import se.citerus.dddsample.infrastructure.persistence.inmemory.CargoRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.HandlingEventRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CargoLifecycleScenarioTest {

  /**
   * Repository implementations are part of the infrastructure layer,
   * which in this test is stubbed out by in-memory replacements.
   */
  HandlingEventRepository handlingEventRepository = new HandlingEventRepositoryInMem();
  CargoRepository cargoRepository = new CargoRepositoryInMem();
  LocationRepository locationRepository = new LocationRepositoryInMem();
  VoyageRepository voyageRepository = new VoyageRepositoryInMem();

  HandlingEventFactory handlingEventFactory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);
  CargoFactory cargoFactory = new CargoFactory(cargoRepository, locationRepository);

  /**
   * This is a domain service interface, whose implementation
   * is part of the infrastructure layer (remote call to external system).
   *
   * It is stubbed in this test.
   */
  RoutingService routingService = new ScenarioStubRoutingService();

  TrackingId trackingId;

  /* The tracking id can be used to lookup the cargo in the repository.

     Important: The cargo, and thus the domain model, is responsible for determining
     the status of the cargo, whether it is on the right track or not and so on.
     This is core domain logic.

     Tracking the cargo basically amounts to presenting information extracted from
     the cargo aggregate in a suitable way. */
  /* Test setup: A cargo should be shipped from Hongkong to Stockholm,
     and it should arrive in no more than two weeks. */
  /* Use case 2: routing

     A number of possible routes for this cargo is requested and may be
     presented to the customer in some way for him/her to choose from.
     Selection could be affected by things like price and time of delivery,
     but this test simply uses an arbitrary selection to mimic that process.

     The cargo is then assigned to the selected route, described by an itinerary. */

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

  @Test
  public void cargoIsMisdirectedAndRerouted() throws Exception {
    bookCargoFromHongkongToStockholm();
    checkDeliveryAfterBooking();


    // Initial route: Hongkong - New York - Chicago - Stockholm
    routeCargoFromHongkongToStockholm();
    checkDeliveryAfterRouting();


    receiveInHongkong();
    checkDeliveryAfterReceiveInHongkong();

    loadInHongkong();
    checkDeliveryAfterLoadInHongKong();

    unloadInTokyo();
    checkDeliveryAfterUnloadInTokyo();


    // Reroute: Tokyo - Hamburg - Stockholm
    specifyNewRouteFromTokyoToStockholm();
    checkDeliveryAfterNewRouteSpecified();

    assignToNewRouteFromTokyoToStockholm();
    checkDeliveryAfterAssignedToNewRoute();


    loadInTokyo();
    checkDeliveryAfterLoadInTokyo();

    unloadInHamburg();
    checkDeliveryAfterUnloadInHamburg();

    loadInHamburg();
    checkDeliveryAfterLoadInHamburg();

    unloadInStockholm();
    checkDeliveryAfterUnloadInStockholm();

    claimInStockholm();
    checkDeliveryAfterClaimInStockholm();
  }

  private void bookCargoFromHongkongToStockholm() {
    Date arrivalDeadline = toDate("2009-03-18");
    Cargo cargo = cargoFactory.newCargo(HONGKONG.unLocode(), STOCKHOLM.unLocode(), arrivalDeadline);
    cargoRepository.store(cargo);

    trackingId = cargo.trackingId();
  }

  private void routeCargoFromHongkongToStockholm() {
    Cargo cargo = cargoRepository.find(trackingId);
    List<Itinerary> itineraries = routingService.fetchRoutesForSpecification(cargo.routeSpecification());
    Itinerary itinerary = itineraries.get(0);
    cargo.assignToRoute(itinerary);
    cargoRepository.store(cargo);
  }

  public void checkDeliveryAfterBooking() throws Exception {
    Cargo cargo = cargoRepository.find(trackingId);

    assertNotNull(cargo);
    assertEquals(NOT_RECEIVED, cargo.delivery().transportStatus());
    assertEquals(NOT_ROUTED, cargo.delivery().routingStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertNull(cargo.delivery().estimatedTimeOfArrival());
    assertNull(cargo.delivery().nextExpectedActivity());
  }

  public void checkDeliveryAfterRouting() throws Exception {
    Cargo cargo = cargoRepository.find(trackingId);

    assertEquals(NOT_RECEIVED, cargo.delivery().transportStatus());
    assertEquals(ROUTED, cargo.delivery().routingStatus());
    assertNotNull(cargo.delivery().estimatedTimeOfArrival());
    assertEquals(new HandlingActivity(RECEIVE, HONGKONG), cargo.delivery().nextExpectedActivity());
  }

  public void receiveInHongkong() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-01"), null, HONGKONG, RECEIVE);
  }

  private void checkDeliveryAfterReceiveInHongkong() {
    Cargo cargo = cargoRepository.find(trackingId);
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertEquals(HONGKONG, cargo.delivery().lastKnownLocation());
  }

  public void loadInHongkong() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-03"), v100, HONGKONG, LOAD);
  }

  private void checkDeliveryAfterLoadInHongKong() {
    // Check current state - should be ok
    Cargo cargo = cargoRepository.find(trackingId);
    assertEquals(v100, cargo.delivery().currentVoyage());
    assertEquals(HONGKONG, cargo.delivery().lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertEquals(new HandlingActivity(UNLOAD, NEWYORK, v100), cargo.delivery().nextExpectedActivity());
  }

  /*
   Here's an attempt to register a handling event that's not valid
   because there is no voyage with the specified voyage number,
   and there's no location with the specified UN Locode either.

   This attempt will be rejected and will not affect the cargo delivery in any way.
 final VoyageNumber noSuchVoyageNumber = new VoyageNumber("XX000");
 final UnLocode noSuchUnLocode = new UnLocode("ZZZZZ");
 try {
   handlingEventService.registerHandlingEvent(
   toDate("2009-03-05"), trackingId, noSuchVoyageNumber, noSuchUnLocode, LOAD
   );
   fail("Should not be able to register a handling event with invalid location and voyage");
 } catch (CannotCreateHandlingEventException expected) {
 }
  */

  public void unloadInTokyo() throws CannotCreateHandlingEventException {
    // Cargo is now (incorrectly) unloaded in Tokyo
    createHandlingEventAndUpdateAggregates(toDate("2009-03-05"), v100, TOKYO, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInTokyo() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - cargo is misdirected!
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(TOKYO, cargo.delivery().lastKnownLocation());
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertTrue(cargo.delivery().isMisdirected());
    assertNull(cargo.delivery().nextExpectedActivity());
  }

  public void specifyNewRouteFromTokyoToStockholm() {
    // TODO cleaner reroute from "earliest location from where the new route originates"
    Cargo cargo = cargoRepository.find(trackingId);

    // Specify a new route, this time from Tokyo (where it was incorrectly unloaded) to Stockholm
    RouteSpecification fromTokyo = cargo.routeSpecification().withOrigin(TOKYO);
    cargo.specifyNewRoute(fromTokyo);
    cargoRepository.store(cargo);
  }

  public void checkDeliveryAfterNewRouteSpecified() {
    Cargo cargo = cargoRepository.find(trackingId);

    // The old itinerary does not satisfy the new specification
    assertEquals(MISROUTED, cargo.delivery().routingStatus());
    assertNull(cargo.delivery().nextExpectedActivity());
  }

  public void assignToNewRouteFromTokyoToStockholm() {
    Cargo cargo = cargoRepository.find(trackingId);

    // Repeat procedure of selecting one out of a number of possible routes satisfying the route spec
    List<Itinerary> newItineraries = routingService.fetchRoutesForSpecification(cargo.routeSpecification());
    Itinerary newItinerary = newItineraries.get(0);

    cargo.assignToRoute(newItinerary);
    cargoRepository.store(cargo);
  }

  public void checkDeliveryAfterAssignedToNewRoute() {
    Cargo cargo = cargoRepository.find(trackingId);

    // New itinerary should satisfy new route
    assertEquals(ROUTED, cargo.delivery().routingStatus());

    // TODO we can't handle the face that after a reroute, the cargo isn't misdirected anymore
    //assertFalse(cargo.isMisdirected());
    //assertEquals(new HandlingActivity(LOAD, TOKYO), cargo.nextExpectedActivity());
  }

  public void loadInTokyo() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-08"), v300, TOKYO, LOAD);
  }

  private void checkDeliveryAfterLoadInTokyo() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertEquals(v300, cargo.delivery().currentVoyage());
    assertEquals(TOKYO, cargo.delivery().lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertEquals(new HandlingActivity(UNLOAD, HAMBURG, v300), cargo.delivery().nextExpectedActivity());
  }

  public void unloadInHamburg() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-12"), v300, HAMBURG, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInHamburg() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(HAMBURG, cargo.delivery().lastKnownLocation());
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertEquals(new HandlingActivity(LOAD, HAMBURG, v400), cargo.delivery().nextExpectedActivity());
  }

  public void loadInHamburg() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-14"), v400, HAMBURG, LOAD);
  }

  private void checkDeliveryAfterLoadInHamburg() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertEquals(v400, cargo.delivery().currentVoyage());
    assertEquals(HAMBURG, cargo.delivery().lastKnownLocation());
    assertEquals(ONBOARD_CARRIER, cargo.delivery().transportStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertEquals(new HandlingActivity(UNLOAD, STOCKHOLM, v400), cargo.delivery().nextExpectedActivity());
  }

  public void unloadInStockholm() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-15"), v400, STOCKHOLM, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInStockholm() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(STOCKHOLM, cargo.delivery().lastKnownLocation());
    assertEquals(IN_PORT, cargo.delivery().transportStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertEquals(new HandlingActivity(CLAIM, STOCKHOLM), cargo.delivery().nextExpectedActivity());
  }

  private void claimInStockholm() throws CannotCreateHandlingEventException {
    // Finally, cargo is claimed in Stockholm. This ends the cargo lifecycle from our perspective.
    createHandlingEventAndUpdateAggregates(toDate("2009-03-16"), null, STOCKHOLM, CLAIM);
  }

  private void checkDeliveryAfterClaimInStockholm() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertEquals(NONE, cargo.delivery().currentVoyage());
    assertEquals(STOCKHOLM, cargo.delivery().lastKnownLocation());
    assertEquals(CLAIMED, cargo.delivery().transportStatus());
    assertFalse(cargo.delivery().isMisdirected());
    assertNull(cargo.delivery().nextExpectedActivity());
  }

  private void createHandlingEventAndUpdateAggregates(Date completionTime, Voyage voyage, Location location, HandlingEvent.Type type) throws CannotCreateHandlingEventException {
    VoyageNumber voyageNumber = voyage != null ? voyage.voyageNumber() : null;
    HandlingEvent handlingEvent = handlingEventFactory.createHandlingEvent(new Date(), completionTime, trackingId, voyageNumber, location.unLocode(), type);
    handlingEventRepository.store(handlingEvent);
    updateCargoAggregate();
  }

  private void updateCargoAggregate() {
    Cargo cargo = cargoRepository.find(trackingId);
    HandlingHistory handlingHistory = handlingEventRepository.lookupHandlingHistoryOfCargo(cargo);
    cargo.deriveDeliveryProgress(handlingHistory);
    cargoRepository.store(cargo);
  }

  private static class ScenarioStubRoutingService implements RoutingService {

    public List<Itinerary> fetchRoutesForSpecification(RouteSpecification routeSpecification) {
      if (routeSpecification.origin().equals(HONGKONG)) {
        // Hongkong - NYC - Chicago - Stockholm, initial routing
        return Arrays.asList(
          new Itinerary(Arrays.asList(
            new Leg(v100, HONGKONG, NEWYORK, toDate("2009-03-03"), toDate("2009-03-09")),
            new Leg(v200, NEWYORK, CHICAGO, toDate("2009-03-10"), toDate("2009-03-14")),
            new Leg(v200, CHICAGO, STOCKHOLM, toDate("2009-03-07"), toDate("2009-03-11"))
          ))
        );
      } else {
        // Tokyo - Hamburg - Stockholm, rerouting misdirected cargo from Tokyo
        return Arrays.asList(
          new Itinerary(Arrays.asList(
            new Leg(v300, TOKYO, HAMBURG, toDate("2009-03-08"), toDate("2009-03-12")),
            new Leg(v400, HAMBURG, STOCKHOLM, toDate("2009-03-14"), toDate("2009-03-15"))
          ))
        );
      }
    }

  }

}
