package se.citerus.dddsample.application.service.dto.assembler;

import junit.framework.TestCase;
import static org.easymock.EasyMock.*;
import se.citerus.dddsample.application.persistence.VoyageRepositoryInMem;
import se.citerus.dddsample.application.remoting.dto.ItineraryCandidateDTO;
import se.citerus.dddsample.application.remoting.dto.LegDTO;
import se.citerus.dddsample.application.remoting.dto.assembler.ItineraryCandidateDTOAssembler;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.CM001;
import static se.citerus.dddsample.domain.model.carrier.SampleVoyages.CM002;
import se.citerus.dddsample.domain.model.carrier.VoyageRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import se.citerus.dddsample.domain.model.location.UnLocode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ItineraryCandidateDTOAssemblerTest extends TestCase {

  public void testToDTO() throws Exception {
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;

    final Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(CM001, origin, SHANGHAI, new Date(), new Date()),
        new Leg(CM002, ROTTERDAM, destination, new Date(), new Date())
      )
    );

    final ItineraryCandidateDTO dto = assembler.toDTO(itinerary);

    assertEquals(2, dto.getLegs().size());
    LegDTO legDTO = dto.getLegs().get(0);
    assertEquals("ABC", legDTO.getVoyageNumber());
    assertEquals("SESTO", legDTO.getFrom());
    assertEquals("CNSHA", legDTO.getTo());

    legDTO = dto.getLegs().get(1);
    assertEquals("ABC", legDTO.getVoyageNumber());
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

    final VoyageRepository voyageRepository = new VoyageRepositoryInMem();

    replay(locationRepository);


    // Tested call
    final Itinerary itinerary = assembler.fromDTO(new ItineraryCandidateDTO(legs), voyageRepository, locationRepository);

    
    assertNotNull(itinerary);
    assertNotNull(itinerary.legs());
    assertEquals(2, itinerary.legs().size());

    final Leg leg1 = itinerary.legs().get(0);
    assertNotNull(leg1);
    assertEquals(HONGKONG, leg1.loadLocation());
    assertEquals(TOKYO, leg1.unloadLocation());

    final Leg leg2 = itinerary.legs().get(1);
    assertNotNull(leg2);
    assertEquals(TOKYO, leg2.loadLocation());
    assertEquals(CHICAGO, leg2.unloadLocation());
  }
}
