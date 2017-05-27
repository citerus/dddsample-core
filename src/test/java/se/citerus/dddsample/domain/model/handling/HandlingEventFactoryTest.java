package se.citerus.dddsample.domain.model.handling;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

public class HandlingEventFactoryTest extends TestCase {

  HandlingEventFactory factory;
  CargoRepository cargoRepository;
  VoyageRepository voyageRepository;
  LocationRepository locationRepository;
  TrackingId trackingId;
  Cargo cargo;

  protected void setUp() throws Exception {

    cargoRepository = createMock(CargoRepository.class);
    voyageRepository = new VoyageRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    factory = new HandlingEventFactory(cargoRepository, voyageRepository, locationRepository);



    trackingId = new TrackingId("ABC");
    RouteSpecification routeSpecification = new RouteSpecification(TOKYO, HELSINKI, new Date());
    cargo = new Cargo(trackingId, routeSpecification);
  }

  public void testCreateHandlingEventWithCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    VoyageNumber voyageNumber = CM001.voyageNumber();
    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(), new Date(100), trackingId, voyageNumber, unLocode, Type.LOAD
    );

    assertThat(handlingEvent).isNotNull();
    assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
    assertThat(handlingEvent.voyage()).isEqualTo(CM001);
    assertThat(handlingEvent.cargo()).isEqualTo(cargo);
    assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
    assertThat(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1))).isTrue();
  }

  public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(), new Date(100), trackingId, null, unLocode, Type.CLAIM
    );

    assertThat(handlingEvent).isNotNull();
    assertThat(handlingEvent.location()).isEqualTo(STOCKHOLM);
    assertThat(handlingEvent.voyage()).isEqualTo(Voyage.NONE);
    assertThat(handlingEvent.cargo()).isEqualTo(cargo);
    assertThat(handlingEvent.completionTime()).isEqualTo(new Date(100));
    assertThat(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1))).isTrue();
  }

  public void testCreateHandlingEventUnknownLocation() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode invalid = new UnLocode("NOEXT");
    try {
      factory.createHandlingEvent(
        new Date(), new Date(100), trackingId, CM001.voyageNumber(), invalid, Type.LOAD
      );
      fail("Expected UnknownLocationException");
    } catch (UnknownLocationException expected) {}
  }

  public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    try {
      VoyageNumber invalid = new VoyageNumber("XXX");
      factory.createHandlingEvent(
        new Date(), new Date(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownVoyageException");
    } catch (UnknownVoyageException expected) {}
  }

  public void testCreateHandlingEventUnknownTrackingId() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(null);

    replay(cargoRepository);

    try {
      factory.createHandlingEvent(
        new Date(), new Date(100), trackingId, CM001.voyageNumber(), STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownCargoException");
    } catch (UnknownCargoException expected) {}
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository);
  }
}
