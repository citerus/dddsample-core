package se.citerus.dddsample.tracking.core.domain.model.handling;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.tracking.core.domain.model.cargo.Cargo;
import se.citerus.dddsample.tracking.core.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.tracking.core.domain.model.cargo.RouteSpecification;
import se.citerus.dddsample.tracking.core.domain.model.cargo.TrackingId;
import static se.citerus.dddsample.tracking.core.domain.model.handling.HandlingEvent.Type;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.tracking.core.domain.model.location.UnLocode;
import se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages;
import se.citerus.dddsample.tracking.core.domain.model.voyage.Voyage;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.util.Date;

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

    VoyageNumber voyageNumber = SampleVoyages.pacific1.voyageNumber();
    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(100), trackingId, voyageNumber, unLocode, Type.LOAD, new OperatorCode("ABCDE")
    );

    assertNotNull(handlingEvent);
    assertEquals(STOCKHOLM, handlingEvent.location());
    assertEquals(SampleVoyages.pacific1, handlingEvent.voyage());
    assertEquals(cargo, handlingEvent.cargo());
    assertEquals(new Date(100), handlingEvent.completionTime());
    assertTrue(handlingEvent.registrationTime().before(new Date(System.currentTimeMillis() + 1)));
  }

  public void testCreateHandlingEventWithoutCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    UnLocode unLocode = STOCKHOLM.unLocode();
    HandlingEvent handlingEvent = factory.createHandlingEvent(
      new Date(100), trackingId, null, unLocode, Type.CLAIM, new OperatorCode("ABCDE")
    );

    assertNotNull(handlingEvent);
    assertEquals(STOCKHOLM, handlingEvent.location());
    assertEquals(Voyage.NONE, handlingEvent.voyage());
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
        new Date(100), trackingId, SampleVoyages.pacific1.voyageNumber(), invalid, Type.LOAD, new OperatorCode("ABCDE")
      );
      fail("Expected UnknownLocationException");
    } catch (UnknownLocationException expected) {
    }
  }

  public void testCreateHandlingEventUnknownCarrierMovement() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(cargo);

    replay(cargoRepository);

    try {
      VoyageNumber invalid = new VoyageNumber("XXX");
      factory.createHandlingEvent(
        new Date(100), trackingId, invalid, STOCKHOLM.unLocode(), Type.LOAD, new OperatorCode("ABCDE")
      );
      fail("Expected UnknownVoyageException");
    } catch (UnknownVoyageException expected) {
    }
  }

  public void testCreateHandlingEventUnknownTrackingId() throws Exception {
    expect(cargoRepository.find(trackingId)).andReturn(null);

    replay(cargoRepository);

    try {
      factory.createHandlingEvent(
        new Date(100), trackingId, SampleVoyages.pacific1.voyageNumber(), STOCKHOLM.unLocode(), Type.LOAD, new OperatorCode("ABCDE")
      );
      fail("Expected UnknownCargoException");
    } catch (UnknownCargoException expected) {
    }
  }

  protected void tearDown() throws Exception {
    verify(cargoRepository);
  }
}
