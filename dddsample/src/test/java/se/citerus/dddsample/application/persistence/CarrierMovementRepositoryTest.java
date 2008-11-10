package se.citerus.dddsample.application.persistence;

import se.citerus.dddsample.domain.model.carrier.Voyage;
import se.citerus.dddsample.domain.model.carrier.VoyageNumber;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;

public class CarrierMovementRepositoryTest extends AbstractRepositoryTest {

  VoyageRepository voyageRepository;

  public void setVoyageRepository(VoyageRepository voyageRepository) {
    this.voyageRepository = voyageRepository;
  }

  public void testFind() throws Exception {
    Voyage voyage = voyageRepository.find(new VoyageNumber("0101"));
    assertNotNull(voyage);
    assertEquals("0101", voyage.voyageNumber().idString());
    /* TODO adapt
    assertEquals(STOCKHOLM, carrierMovement.departureLocation());
    assertEquals(HELSINKI, carrierMovement.arrivalLocation());
    assertEquals(DateTestUtil.toDate("2007-09-23", "02:00"), carrierMovement.departureTime());
    assertEquals(DateTestUtil.toDate("2007-09-23", "03:00"), carrierMovement.arrivalTime());
    */
  }

}
