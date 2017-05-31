package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.inmemory.VoyageRepositoryInMem;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.RouteCandidateDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.easymock.EasyMock.*;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

public class ItineraryCandidateDTOAssemblerTest extends TestCase {

  public void testToDTO() throws Exception {
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;

    final Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(CM001, origin, SHANGHAI, new Date(), new Date()),
        new Leg(CM001, ROTTERDAM, destination, new Date(), new Date())
      )
    );

    final RouteCandidateDTO dto = assembler.toDTO(itinerary);

    assertThat(dto.getLegs()).hasSize(2);
    LegDTO legDTO = dto.getLegs().get(0);
    assertThat(legDTO.getVoyageNumber()).isEqualTo("CM001");
    assertThat(legDTO.getFrom()).isEqualTo("SESTO");
    assertThat(legDTO.getTo()).isEqualTo("CNSHA");

    legDTO = dto.getLegs().get(1);
    assertThat(legDTO.getVoyageNumber()).isEqualTo("CM001");
    assertThat(legDTO.getFrom()).isEqualTo("NLRTM");
    assertThat(legDTO.getTo()).isEqualTo("AUMEL");
  }

  public void testFromDTO() throws Exception {
    final ItineraryCandidateDTOAssembler assembler = new ItineraryCandidateDTOAssembler();

    final List<LegDTO> legs = new ArrayList<LegDTO>();
    legs.add(new LegDTO("CM001", "AAAAA", "BBBBB", new Date(), new Date()));
    legs.add(new LegDTO("CM001", "BBBBB", "CCCCC", new Date(), new Date()));

    final LocationRepository locationRepository = createMock(LocationRepository.class);
    expect(locationRepository.find(new UnLocode("AAAAA"))).andReturn(HONGKONG);
    expect(locationRepository.find(new UnLocode("BBBBB"))).andReturn(TOKYO).times(2);
    expect(locationRepository.find(new UnLocode("CCCCC"))).andReturn(CHICAGO);

    final VoyageRepository voyageRepository = new VoyageRepositoryInMem();

    replay(locationRepository);


    // Tested call
    final Itinerary itinerary = assembler.fromDTO(new RouteCandidateDTO(legs), voyageRepository, locationRepository);

    
    assertThat(itinerary).isNotNull();
    assertThat(itinerary.legs()).isNotNull();
    assertThat(itinerary.legs()).hasSize(2);

    final Leg leg1 = itinerary.legs().get(0);
    assertThat(leg1).isNotNull();
    assertThat(leg1.loadLocation()).isEqualTo(HONGKONG);
    assertThat(leg1.unloadLocation()).isEqualTo(TOKYO);

    final Leg leg2 = itinerary.legs().get(1);
    assertThat(leg2).isNotNull();
    assertThat(leg2.loadLocation()).isEqualTo(TOKYO);
    assertThat(leg2.unloadLocation()).isEqualTo(CHICAGO);
  }
}
