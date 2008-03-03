package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.Location;
import se.citerus.dddsample.domain.UnLocode;

public class CarrierMovementRepositoryTest extends AbstractRepositoryTest {

  CarrierMovementRepository carrierMovementRepository;
  private final Location stockholm = new Location(new UnLocode("SE","STO"), "Stockholm");
  private final Location helsinki = new Location(new UnLocode("FI","HEL"), "Helsinki");

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void testFind() throws Exception {
    CarrierMovement carrierMovement = carrierMovementRepository.find(new CarrierMovementId("CAR_001"));
    assertNotNull(carrierMovement);
    assertEquals("CAR_001", carrierMovement.carrierId().idString());
    assertEquals(stockholm, carrierMovement.from());
    assertEquals(helsinki, carrierMovement.to());
  }

}
