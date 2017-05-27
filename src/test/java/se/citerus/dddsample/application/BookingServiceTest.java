package se.citerus.dddsample.application;

import junit.framework.TestCase;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;
import static se.citerus.dddsample.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

public class BookingServiceTest extends TestCase {

  BookingServiceImpl bookingService;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  RoutingService routingService;

  protected void setUp() throws Exception {
    cargoRepository = createMock(CargoRepository.class);
    locationRepository = createMock(LocationRepository.class);
    routingService = createMock(RoutingService.class);
    bookingService = new BookingServiceImpl(cargoRepository, locationRepository, routingService);
  }

  public void testRegisterNew() {
    TrackingId expectedTrackingId = new TrackingId("TRK1");
    UnLocode fromUnlocode = new UnLocode("USCHI");
    UnLocode toUnlocode = new UnLocode("SESTO");

    expect(cargoRepository.nextTrackingId()).andReturn(expectedTrackingId);
    expect(locationRepository.find(fromUnlocode)).andReturn(CHICAGO);
    expect(locationRepository.find(toUnlocode)).andReturn(STOCKHOLM);

    cargoRepository.store(isA(Cargo.class));

    replay(cargoRepository, locationRepository);

    TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, new Date());
    assertThat(trackingId).isEqualTo(expectedTrackingId);
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, locationRepository);
  }
}
