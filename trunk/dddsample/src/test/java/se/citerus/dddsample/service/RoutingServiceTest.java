package se.citerus.dddsample.service;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.Cargo;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;
import static se.citerus.dddsample.domain.SampleLocations.*;
import se.citerus.dddsample.domain.TrackingId;
import se.citerus.dddsample.repository.CargoRepository;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;

import java.util.List;

public class RoutingServiceTest extends TestCase {

  private RoutingServiceImpl routingService;
  private LocationRepository locationRepository;
  private CargoRepository cargoRepository;
  private CarrierMovementRepository carrierMovementRepository;

  protected void setUp() throws Exception {
    routingService = new RoutingServiceImpl();
    locationRepository = createMock(LocationRepository.class);
    routingService.setLocationRepository(locationRepository);
    cargoRepository = createMock(CargoRepository.class);
    routingService.setCargoRepository(cargoRepository);
    carrierMovementRepository = createMock(CarrierMovementRepository.class);
    routingService.setCarrierMovementRepository(carrierMovementRepository);
  }

  public void testCalculatePossibleRoutes() {
    TrackingId trackingId = new TrackingId("ABC");
    Cargo cargo = new Cargo(trackingId, HONGKONG, HELSINKI);

    expect(locationRepository.findAll()).andStubReturn(getAll());
    expect(cargoRepository.find(isA(TrackingId.class))).andReturn(cargo);
    expect(carrierMovementRepository.find(isA(CarrierMovementId.class))).
      andStubReturn(new CarrierMovement(new CarrierMovementId("CM"), CHICAGO, HAMBURG));
    
    replay(locationRepository, cargoRepository, carrierMovementRepository);

    List<ItineraryCandidateDTO> candidates = routingService.calculatePossibleRoutes(trackingId, null);
    assertNotNull(candidates);
    
    for (ItineraryCandidateDTO itinerary : candidates) {
      List<LegDTO> legs = itinerary.getLegs();
      assertNotNull(legs);
      assertFalse(legs.isEmpty());

      // Cargo origin and start of first leg should match
      String firstLegStart = legs.get(0).getFrom();
      assertEquals(cargo.origin().unLocode().idString(), firstLegStart);

      // Cargo final destination and last leg stop should match
      String lastLegStop = legs.get(legs.size() - 1).getTo();
      assertEquals(cargo.finalDestination().unLocode().idString(), lastLegStop);

      for (int i = 0; i < legs.size() - 1; i++) {
        // Assert that all legs are conencted
        assertEquals(legs.get(i).getTo(), legs.get(i + 1).getFrom());
      }
    }

    verify(locationRepository, cargoRepository, carrierMovementRepository);
  }

}
