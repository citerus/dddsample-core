package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.*;

public class CarrierMovementRepositoryTest extends AbstractRepositoryTest {

  CarrierMovementRepository carrierMovementRepository;

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void testFind() throws Exception {
    CarrierMovement carrierMovement = carrierMovementRepository.find(new CarrierMovementId("CAR_001"));
    assertNotNull(carrierMovement);
    assertEquals("CAR_001", carrierMovement.carrierId().idString());
    assertEquals(STOCKHOLM, carrierMovement.from());
    assertEquals(HELSINKI, carrierMovement.to());
  }

}
