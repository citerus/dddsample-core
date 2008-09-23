package se.citerus.dddsample.domain.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.impl.BookingServiceImpl;

public class BookingServiceTest extends TestCase {

  BookingServiceImpl cargoService;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;

  protected void setUp() throws Exception {
    cargoService = new BookingServiceImpl();
    cargoRepository = createMock(CargoRepository.class);
    locationRepository = createMock(LocationRepository.class);
    cargoService.setCargoRepository(cargoRepository);
    cargoService.setLocationRepository(locationRepository);
  }

  public void testRegisterNew() {
    TrackingId expectedTrackingId = new TrackingId("TRK1");
    UnLocode fromUnlocode = new UnLocode("USCHI");
    UnLocode toUnlocode = new UnLocode("SESTO");

    expect(cargoRepository.nextTrackingId()).andReturn(expectedTrackingId);
    expect(locationRepository.find(fromUnlocode)).andReturn(CHICAGO);
    expect(locationRepository.find(toUnlocode)).andReturn(STOCKHOLM);

    cargoRepository.save(isA(Cargo.class));

    replay(cargoRepository, locationRepository);

    TrackingId trackingId = cargoService.bookNewCargo(fromUnlocode, toUnlocode);
    assertEquals(expectedTrackingId, trackingId);
  }

  public void testRegisterNewNullArguments() {
    replay(cargoRepository, locationRepository);
    try {
      cargoService.bookNewCargo(null, null);
      fail("Null arguments should not be allowed");
    } catch (IllegalArgumentException expected) {}
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, locationRepository);
  }
}
