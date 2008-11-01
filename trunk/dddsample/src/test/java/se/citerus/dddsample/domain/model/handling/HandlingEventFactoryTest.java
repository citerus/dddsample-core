package se.citerus.dddsample.domain.model.handling;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.persistence.CarrierMovementRepositoryInMem;
import se.citerus.dddsample.application.persistence.LocationRepositoryInMem;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import static se.citerus.dddsample.domain.model.carrier.SampleCarrierMovements.CM001;
import static se.citerus.dddsample.domain.model.handling.HandlingEvent.Type;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.service.UnknownCarrierMovementIdException;
import se.citerus.dddsample.domain.service.UnknownLocationException;
import se.citerus.dddsample.domain.service.UnknownTrackingIdException;

import java.util.Date;

public class HandlingEventFactoryTest extends TestCase {

  HandlingEventFactory factory;
  CargoRepository cargoRepository;
  CarrierMovementRepository carrierMovementRepository;
  LocationRepository locationRepository;
  TrackingId trackingId;
  Cargo cargo;

  protected void setUp() throws Exception {

    cargoRepository = createMock(CargoRepository.class);
    carrierMovementRepository = new CarrierMovementRepositoryInMem();
    locationRepository = new LocationRepositoryInMem();
    factory = new HandlingEventFactory(cargoRepository, carrierMovementRepository, locationRepository);



    trackingId = new TrackingId("ABC");
    cargo = new Cargo(trackingId, TOKYO, HELSINKI);
  }

  public void testCreateHandlingEventWithCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    CarrierMovementId carrierMovementId = CM001.carrierMovementId();
    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(100), trackingId, carrierMovementId, unLocode, Type.LOAD
    );

    assertNotNull(handlingEvent);
    assertEquals(STOCKHOLM, handlingEvent.location());
    assertEquals(CM001, handlingEvent.carrierMovement());
    assertEquals(cargo, handlingEvent.cargo());
    assertEquals(new Date(100), handlingEvent.completionTime());
    assertTrue(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1)));
  }

  public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(100), trackingId, null, unLocode, Type.CLAIM
    );

    assertNotNull(handlingEvent);
    assertEquals(STOCKHOLM, handlingEvent.location());
    assertEquals(CarrierMovement.NONE, handlingEvent.carrierMovement());
    assertEquals(cargo, handlingEvent.cargo());
    assertEquals(new Date(100), handlingEvent.completionTime());
    assertTrue(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1)));
  }

  public void testCreateHandlingEventUnknownLocation() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode invalid = new UnLocode("NOEXT");
    try {
      factory.createHandlingEvent(
        new Date(100), trackingId, CM001.carrierMovementId(), invalid, Type.LOAD
      );
      fail("Expected UnknownLocationException");
    } catch (UnknownLocationException expected) {}
  }

  public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    try {
      CarrierMovementId invalid = new CarrierMovementId("XXX");
      factory.createHandlingEvent(
        new Date(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownCarrierMovementIdException");
    } catch (UnknownCarrierMovementIdException expected) {}
  }

  public void testCreateHandlingEventUnknownTrackingId() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(null);

    replay(cargoRepository);

    try {
      factory.createHandlingEvent(
        new Date(100), trackingId, CM001.carrierMovementId(), STOCKHOLM.unLocode(), Type.LOAD
      );
      fail("Expected UnknownTrackingIdException");
    } catch (UnknownTrackingIdException expected) {}
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository);
  }
}
