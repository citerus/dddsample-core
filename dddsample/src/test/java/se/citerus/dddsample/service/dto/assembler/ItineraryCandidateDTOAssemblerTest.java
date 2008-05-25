package se.citerus.dddsample.service.dto.assembler;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.domain.*;
import static se.citerus.dddsample.domain.SampleLocations.*;
import se.citerus.dddsample.repository.CarrierMovementRepository;
import se.citerus.dddsample.repository.LocationRepository;
import se.citerus.dddsample.service.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.service.dto.LegDTO;

import java.util.ArrayList;
import java.util.List;

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

  public void testFromDTO() throws Exception {
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    final List<LegDTO> legs = new ArrayList<LegDTO>();
    legs.add(new LegDTO("CM1", "AAAAA", "BBBBB"));
    legs.add(new LegDTO("CM2", "BBBBB", "CCCCC"));

    final LocationRepository locationRepository = createMock(LocationRepository.class);
    expect(locationRepository.find(new UnLocode("AAAAA"))).andReturn(HONGKONG);
    expect(locationRepository.find(new UnLocode("BBBBB"))).andReturn(TOKYO).times(2);
    expect(locationRepository.find(new UnLocode("CCCCC"))).andReturn(CHICAGO);

    final CarrierMovementRepository carrierMovementRepository = createMock(CarrierMovementRepository.class);
    final CarrierMovementId cmId1 = new CarrierMovementId("CM1");
    final CarrierMovementId cmId2 = new CarrierMovementId("CM2");
    final CarrierMovement cm1 = new CarrierMovement(cmId1, HONGKONG, TOKYO);
    final CarrierMovement cm2 = new CarrierMovement(cmId2, TOKYO, CHICAGO);

    expect(carrierMovementRepository.find(cmId1)).andReturn(cm1);
    expect(carrierMovementRepository.find(cmId2)).andReturn(cm2);

    replay(locationRepository, carrierMovementRepository);


    // Tested call
    final Itinerary itinerary = assembler.fromDTO(new ItineraryCandidateDTO(legs), carrierMovementRepository, locationRepository);

    
    assertNotNull(itinerary);
    assertNotNull(itinerary.legs());
    assertEquals(2, itinerary.legs().size());

    final Leg leg1 = itinerary.legs().get(0);
    assertNotNull(leg1);
    assertEquals(HONGKONG, leg1.from());
    assertEquals(TOKYO, leg1.to());
    assertEquals(cm1, leg1.carrierMovement());

    final Leg leg2 = itinerary.legs().get(1);
    assertNotNull(leg2);
    assertEquals(TOKYO, leg2.from());
    assertEquals(CHICAGO, leg2.to());
    assertEquals(cm2, leg2.carrierMovement());
  }
}
