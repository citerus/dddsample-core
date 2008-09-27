package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.DateTestUtil;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HELSINKI;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

public class CarrierMovementRepositoryTest extends AbstractRepositoryTest {

  CarrierMovementRepository carrierMovementRepository;

  public void setCarrierMovementRepository(CarrierMovementRepository carrierMovementRepository) {
    this.carrierMovementRepository = carrierMovementRepository;
  }

  public void testFind() throws Exception {
    CarrierMovement carrierMovement = carrierMovementRepository.find(new CarrierMovementId("CAR_001"));
    assertNotNull(carrierMovement);
    assertEquals("CAR_001", carrierMovement.carrierMovementId().idString());
    assertEquals(STOCKHOLM, carrierMovement.from());
    assertEquals(HELSINKI, carrierMovement.to());
    assertEquals(DateTestUtil.toDate("2007-09-23", "02:00"), carrierMovement.departureTime());
    assertEquals(DateTestUtil.toDate("2007-09-23", "03:00"), carrierMovement.arrivalTime());
  }

}
