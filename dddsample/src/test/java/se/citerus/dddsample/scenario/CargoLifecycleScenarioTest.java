package se.citerus.dddsample.scenario;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
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
import se.citerus.dddsample.domain.model.shared.HandlingActivity;
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
   * <p/>
   * It is stubbed in this test.
   */
  RoutingService routingService = new ScenarioStubRoutingService();

  TrackingId trackingId;

  @Test
  public void cargoIsCorrectlyDelivered() throws Exception {
    bookCargoFromHongkongToStockholm();
    checkDeliveryAfterBooking();

    // Route: Hongkong - Long Beach - New York - Stockholm
    routeCargoFromHongkongToStockholm();
    checkDeliveryAfterRouting();


    receiveInHongkong();
    checkDeliveryAfterReceiveInHongkong();

    loadInHongkong();
    checkDeliveryAfterLoadInHongKong();

    unloadInLongBeach();
    checkDeliveryAfterUnloadInLongBeach();

    loadInLongBeach();
    checkDeliveryAfterLoadInLongBeach();

    unloadInNewYork();
    checkDeliveryAfterUnloadInNewYork();

    loadInNewYork();
    checkDeliveryAfterLoadInNewYork();

    unloadInStockholmOffOf(v200);
    checkDeliveryAfterUnloadInStockholm();

    claimInStockholm();
    checkDeliveryAfterClaimInStockholm();
  }

  private void unloadInLongBeach() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-06"), v100, LONGBEACH, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInLongBeach() {
    Cargo cargo = cargoRepository.find(trackingId);

    assertThat(cargo.delivery().currentVoyage(), is(NONE));
    assertThat(cargo.delivery().lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.delivery().transportStatus(), is(IN_PORT));
    assertFalse(cargo.delivery().isMisdirected());
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(LOAD, LONGBEACH, v250)));
  }

  private void loadInLongBeach() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-07"), v250, LONGBEACH, LOAD);
  }

  private void checkDeliveryAfterLoadInLongBeach() {
    Cargo cargo = cargoRepository.find(trackingId);

    assertThat(cargo.delivery().currentVoyage(), is(v250));
    assertThat(cargo.delivery().lastKnownLocation(), is(LONGBEACH));
    assertThat(cargo.delivery().transportStatus(), is(ONBOARD_CARRIER));
    assertFalse(cargo.delivery().isMisdirected());
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(UNLOAD, NEWYORK, v250)));
  }

  private void unloadInNewYork() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-08"), v250, NEWYORK, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInNewYork() {
    Cargo cargo = cargoRepository.find(trackingId);

    assertThat(cargo.delivery().currentVoyage(), is(NONE));
    assertThat(cargo.delivery().lastKnownLocation(), is(NEWYORK));
    assertThat(cargo.delivery().transportStatus(), is(IN_PORT));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(LOAD, NEWYORK, v200)));
    assertFalse(cargo.delivery().isMisdirected());
  }

  private void loadInNewYork() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-10"), v200, NEWYORK, LOAD);
  }

  private void checkDeliveryAfterLoadInNewYork() {
    Cargo cargo = cargoRepository.find(trackingId);

    assertThat(cargo.delivery().currentVoyage(), is(v200));
    assertThat(cargo.delivery().lastKnownLocation(), is(NEWYORK));
    assertThat(cargo.delivery().transportStatus(), is(ONBOARD_CARRIER));
    assertFalse(cargo.delivery().isMisdirected());
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(UNLOAD, STOCKHOLM, v200)));
  }

  @Test
  public void cargoIsMisdirectedAndRerouted() throws Exception {
    bookCargoFromHongkongToStockholm();
    checkDeliveryAfterBooking();


    // Initial route: Hongkong - Long Beach - New York - Stockholm
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

    unloadInStockholmOffOf(v400);
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

    assertThat(cargo.delivery().transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.delivery().routingStatus(), is(NOT_ROUTED));
    assertFalse(cargo.delivery().isMisdirected());
    assertNull(cargo.projections().estimatedTimeOfArrival());
    assertNull(cargo.projections().nextExpectedActivity());
  }

  public void checkDeliveryAfterRouting() throws Exception {
    Cargo cargo = cargoRepository.find(trackingId);

    assertThat(cargo.delivery().transportStatus(), is(NOT_RECEIVED));
    assertThat(cargo.delivery().routingStatus(), is(ROUTED));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(RECEIVE, HONGKONG)));
    assertNotNull(cargo.projections().estimatedTimeOfArrival());
  }

  public void receiveInHongkong() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-01"), null, HONGKONG, RECEIVE);
  }

  private void checkDeliveryAfterReceiveInHongkong() {
    Cargo cargo = cargoRepository.find(trackingId);
    assertThat(cargo.delivery().transportStatus(), is(IN_PORT));
    assertThat(cargo.delivery().lastKnownLocation(), is(HONGKONG));
  }

  public void loadInHongkong() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-03"), v100, HONGKONG, LOAD);
  }

  private void checkDeliveryAfterLoadInHongKong() {
    // Check current state - should be ok
    Cargo cargo = cargoRepository.find(trackingId);

    assertThat(cargo.delivery().currentVoyage(), is(v100));
    assertThat(cargo.delivery().lastKnownLocation(), is(HONGKONG));
    assertThat(cargo.delivery().transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(UNLOAD, LONGBEACH, v100)));
    assertFalse(cargo.delivery().isMisdirected());
  }

  public void unloadInTokyo() throws CannotCreateHandlingEventException {
    // Cargo is now (incorrectly) unloaded in Tokyo
    createHandlingEventAndUpdateAggregates(toDate("2009-03-05"), v100, TOKYO, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInTokyo() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - cargo is misdirected!
    assertThat(cargo.delivery().currentVoyage(), is(NONE));
    assertThat(cargo.delivery().lastKnownLocation(), is(TOKYO));
    assertThat(cargo.delivery().transportStatus(), is(IN_PORT));
    assertTrue(cargo.delivery().isMisdirected());
    assertNull(cargo.projections().nextExpectedActivity());
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
    assertThat(cargo.delivery().routingStatus(), is(MISROUTED));
    assertNull(cargo.projections().nextExpectedActivity());
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
    assertThat(cargo.delivery().routingStatus(), is(ROUTED));
    assertFalse(cargo.delivery().isMisdirected());
    assertEquals(new HandlingActivity(LOAD, TOKYO, v300), cargo.projections().nextExpectedActivity());
  }

  public void loadInTokyo() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-08"), v300, TOKYO, LOAD);
  }

  private void checkDeliveryAfterLoadInTokyo() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertThat(cargo.delivery().currentVoyage(), is(v300));
    assertThat(cargo.delivery().lastKnownLocation(), is(TOKYO));
    assertThat(cargo.delivery().transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(UNLOAD, HAMBURG, v300)));
    assertFalse(cargo.delivery().isMisdirected());
  }

  public void unloadInHamburg() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-12"), v300, HAMBURG, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInHamburg() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok

    assertThat(cargo.delivery().currentVoyage(), is(NONE));
    assertThat(cargo.delivery().lastKnownLocation(), is(HAMBURG));
    assertThat(cargo.delivery().transportStatus(), is(IN_PORT));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(LOAD, HAMBURG, v400)));
    assertFalse(cargo.delivery().isMisdirected());
  }

  public void loadInHamburg() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-14"), v400, HAMBURG, LOAD);
  }

  private void checkDeliveryAfterLoadInHamburg() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok

    assertThat(cargo.delivery().currentVoyage(), is(v400));
    assertThat(cargo.delivery().lastKnownLocation(), is(HAMBURG));
    assertThat(cargo.delivery().transportStatus(), is(ONBOARD_CARRIER));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(UNLOAD, STOCKHOLM, v400)));
    assertFalse(cargo.delivery().isMisdirected());
  }

  public void unloadInStockholmOffOf(Voyage voyage) throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-15"), voyage, STOCKHOLM, UNLOAD);
  }

  private void checkDeliveryAfterUnloadInStockholm() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertThat(cargo.delivery().currentVoyage(), is(NONE));
    assertThat(cargo.delivery().lastKnownLocation(), is(STOCKHOLM));
    assertThat(cargo.delivery().transportStatus(), is(IN_PORT));
    assertThat(cargo.projections().nextExpectedActivity(), is(new HandlingActivity(CLAIM, STOCKHOLM)));
    assertFalse(cargo.delivery().isMisdirected());
  }

  private void claimInStockholm() throws CannotCreateHandlingEventException {
    createHandlingEventAndUpdateAggregates(toDate("2009-03-16"), null, STOCKHOLM, CLAIM);
  }

  private void checkDeliveryAfterClaimInStockholm() {
    Cargo cargo = cargoRepository.find(trackingId);
    // Check current state - should be ok
    assertThat(cargo.delivery().currentVoyage(), is(NONE));
    assertThat(cargo.delivery().lastKnownLocation(), is(STOCKHOLM));
    assertThat(cargo.delivery().transportStatus(), is(CLAIMED));
    assertFalse(cargo.delivery().isMisdirected());
    assertNull(cargo.projections().nextExpectedActivity());
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
            new Leg(v100, HONGKONG, LONGBEACH, toDate("2009-03-03"), toDate("2009-03-09")),
            new Leg(v250, LONGBEACH, NEWYORK, toDate("2009-03-10"), toDate("2009-03-14")),
            new Leg(v200, NEWYORK, STOCKHOLM, toDate("2009-03-07"), toDate("2009-03-11"))
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
