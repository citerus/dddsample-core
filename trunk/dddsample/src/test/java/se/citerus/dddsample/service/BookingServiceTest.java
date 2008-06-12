package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.Cargo;
import static se.citerus.dddsample.domain.SampleLocations.CHICAGO;
import static se.citerus.dddsample.domain.SampleLocations.STOCKHOLM;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.domain.UnLocode;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.LocationRepository;

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

    TrackingId trackingId = cargoService.registerNewCargo(fromUnlocode, toUnlocode);
    assertEquals(expectedTrackingId, trackingId);
  }

  public void testRegisterNewNullArguments() {
    replay(cargoRepository, locationRepository);
    try {
      cargoService.registerNewCargo(null, null);
      fail("Null arguments should not be allowed");
    } catch (IllegalArgumentException expected) {}
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository, locationRepository);
  }
}
