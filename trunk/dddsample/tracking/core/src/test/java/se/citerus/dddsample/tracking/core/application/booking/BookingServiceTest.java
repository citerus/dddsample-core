package se.citerus.dddsample.tracking.core.application.booking;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.service.RoutingService;

import java.util.Date;

public class BookingServiceTest extends TestCase {

  BookingServiceImpl bookingService;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  RoutingService routingService;
  TrackingIdFactory trackingIdFactory;

  protected void setUp() throws Exception {
    cargoRepository = createMock(CargoRepository.class);
    locationRepository = createMock(LocationRepository.class);
    routingService = createMock(RoutingService.class);
    trackingIdFactory = createMock(TrackingIdFactory.class);
    CargoFactory cargoFactory = new CargoFactory(locationRepository, trackingIdFactory);
    bookingService = new BookingServiceImpl(routingService, cargoFactory, cargoRepository, locationRepository);
  }

  public void testRegisterNew() {
    TrackingId expectedTrackingId = new TrackingId("TRK1");
    UnLocode fromUnlocode = new UnLocode("USCHI");
    UnLocode toUnlocode = new UnLocode("SESTO");

    expect(trackingIdFactory.nextTrackingId()).andReturn(expectedTrackingId);
    expect(locationRepository.find(fromUnlocode)).andReturn(CHICAGO);
    expect(locationRepository.find(toUnlocode)).andReturn(STOCKHOLM);

    cargoRepository.store(isA(Cargo.class));

    replay(cargoRepository, locationRepository, trackingIdFactory);

    TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, new Date());
    assertEquals(expectedTrackingId, trackingId);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, locationRepository, trackingIdFactory);
  }
}
