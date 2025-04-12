package se.citerus.dddsample.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.citerus.dddsample.application.impl.BookingServiceImpl;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoFactory;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.RoutingService;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.CHICAGO;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.STOCKHOLM;

public class BookingServiceTest {

  BookingServiceImpl bookingService;
  CargoRepository cargoRepository;
  LocationRepository locationRepository;
  RoutingService routingService;
  CargoFactory cargoFactory;

  @BeforeEach
  public void setUp() {
    cargoRepository = mock(CargoRepository.class);
    locationRepository = mock(LocationRepository.class);
    routingService = mock(RoutingService.class);
    cargoFactory = new CargoFactory(locationRepository, cargoRepository);
    bookingService = new BookingServiceImpl(cargoRepository, locationRepository, routingService, cargoFactory);
  }

  @Test
  public void testRegisterNew() {
    TrackingId expectedTrackingId = new TrackingId("TRK1");
    UnLocode fromUnlocode = new UnLocode("USCHI");
    UnLocode toUnlocode = new UnLocode("SESTO");

    when(cargoRepository.nextTrackingId()).thenReturn(expectedTrackingId);
    when(locationRepository.find(fromUnlocode)).thenReturn(CHICAGO);
    when(locationRepository.find(toUnlocode)).thenReturn(STOCKHOLM);

    TrackingId trackingId = bookingService.bookNewCargo(fromUnlocode, toUnlocode, Instant.now());
    assertThat(trackingId).isEqualTo(expectedTrackingId);
    verify(cargoRepository, times(1)).store(isA(Cargo.class));
    verify(locationRepository, times(2)).find(any(UnLocode.class));
  }
}
