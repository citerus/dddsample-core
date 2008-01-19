package se.citerus.dddsample.repository;

import se.citerus.dddsample.domain.CarrierMovementId;
import se.citerus.dddsample.domain.CarrierMovement;
import se.citerus.dddsample.domain.Location;

/**
 * Created by IntelliJ IDEA.
 * User: Peter Backlund
 * Date: 2008-jan-19
 * Time: 18:41:50
 * To change this template use File | Settings | File Templates.
 */
public class CarrierMovementRepositoryTest extends AbstractRepositoryTest {

  CarrierMovementRepository carrierMovementRepository;

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void testFind() throws Exception {
    CarrierMovement carrierMovement = carrierMovementRepository.find(new CarrierMovementId("CAR_001"));
    assertNotNull(carrierMovement);
    assertEquals("CAR_001", carrierMovement.carrierId().idString());
    assertEquals(new Location("SESTO"), carrierMovement.from());
    assertEquals(new Location("FIHEL"), carrierMovement.to());
  }

}
