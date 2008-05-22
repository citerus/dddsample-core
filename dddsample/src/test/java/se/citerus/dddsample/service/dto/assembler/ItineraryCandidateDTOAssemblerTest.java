package se.citerus.dddsample.service.dto.assembler;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.*;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;

public class ItineraryCandidateDTOAssemblerTest extends TestCase {

  public void testToDTO() throws Exception {
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;

    final CarrierMovement cm = new CarrierMovement(
      new CarrierMovementId("ABC"), origin, destination);

    final Itinerary itinerary = new Itinerary(
      new Leg(cm, origin, SHANGHAI),
      new Leg(cm, ROTTERDAM, destination)
    );

    final ItineraryCandidateDTO dto = assembler.toDTO(itinerary);

    assertEquals(2, dto.getLegs().size());
    LegDTO legDTO = dto.getLegs().get(0);
    assertEquals("ABC", legDTO.getCarrierMovementId());
    assertEquals("SESTO", legDTO.getFrom());
    assertEquals("CNSHA", legDTO.getTo());

    legDTO = dto.getLegs().get(1);
    assertEquals("ABC", legDTO.getCarrierMovementId());
    assertEquals("NLRTM", legDTO.getFrom());
    assertEquals("AUMEL", legDTO.getTo());
  }
}
