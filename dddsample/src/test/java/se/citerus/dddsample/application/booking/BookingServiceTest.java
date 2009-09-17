package se.citerus.dddsample.application.booking;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;

import java.util.Date;

public class BookingServiceTest extends TestCase {

  BookingServiceImpl bookingService;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  RoutingService routingService;
  TrackingIdGenerator trackingIdGenerator;

  protected void setUp() throws Exception {
    cargoRepository = createMock(CargoRepository.class);
    locationRepository = createMock(LocationRepository.class);
    routingService = createMock(RoutingService.class);
    trackingIdGenerator = createMock(TrackingIdGenerator.class);
    CargoFactory cargoFactory = new CargoFactory(locationRepository, trackingIdGenerator);
    bookingService = new BookingServiceImpl(routingService, cargoFactory, cargoRepository, locationRepository);
  }

  public void testRegisterNew() {
    TrackingId expectedTrackingId = new TrackingId("TRK1");
    UnLocode fromUnlocode = new UnLocode("USCHI");
    UnLocode toUnlocode = new UnLocode("SESTO");

    expect(trackingIdGenerator.nextTrackingId()).andReturn(expectedTrackingId);
    expect(locationRepository.find(fromUnlocode)).andReturn(CHICAGO);
    expect(locationRepository.find(toUnlocode)).andReturn(STOCKHOLM);

    cargoRepository.store(isA(Cargo.class));

    replay(cargoRepository, locationRepository, trackingIdGenerator);

    TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, new Date());
    assertEquals(expectedTrackingId, trackingId);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, locationRepository, trackingIdGenerator);
  }
}
