package se.citerus.dddsample.tracking.core.interfaces.booking.facade;

import static org.junit.Assert.*;
import org.junit.Test;
import se.citerus.dddsample.tracking.booking.api.CargoRoutingDTO;
import se.citerus.dddsample.tracking.booking.api.LegDTO;
import se.citerus.dddsample.tracking.booking.api.LocationDTO;
import se.citerus.dddsample.tracking.booking.api.RouteCandidateDTO;
import se.citerus.dddsample.tracking.core.domain.model.cargo.*;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import se.citerus.dddsample.tracking.core.domain.model.location.LocationRepository;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.tracking.core.domain.model.voyage.SampleVoyages.CM001;
import se.citerus.dddsample.tracking.core.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.LocationRepositoryInMem;
import se.citerus.dddsample.tracking.core.infrastructure.persistence.inmemory.VoyageRepositoryInMem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class DTOAssemblerTest {

  @Test
  public void toCargoRoutingDTO() throws Exception {
    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(origin, destination, new Date()));

    final Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(CM001, origin, SHANGHAI, new Date(), new Date()),
        new Leg(CM001, ROTTERDAM, destination, new Date(), new Date())
      )
    );

    cargo.assignToRoute(itinerary);

    final CargoRoutingDTO dto = DTOAssembler.toDTO(cargo);

    assertEquals(2, dto.getLegs().size());

    LegDTO legDTO = dto.getLegs().get(0);
    assertEquals("CM001", legDTO.getVoyageNumber());
    assertEquals("SESTO", legDTO.getFrom());
    assertEquals("CNSHA", legDTO.getTo());

    legDTO = dto.getLegs().get(1);
    assertEquals("CM001", legDTO.getVoyageNumber());
    assertEquals("NLRTM", legDTO.getFrom());
    assertEquals("AUMEL", legDTO.getTo());
  }

  @Test
  public void toCargoDTONoItinerary() throws Exception {
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));
    final CargoRoutingDTO dto = DTOAssembler.toDTO(cargo);

    assertEquals("XYZ", dto.getTrackingId());
    assertEquals("SESTO", dto.getOrigin());
    assertEquals("AUMEL", dto.getFinalDestination());
    assertTrue(dto.getLegs().isEmpty());
  }

  @Test
  public void toRouteCandidateDTO() throws Exception {
    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;

    final Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(CM001, origin, SHANGHAI, new Date(), new Date()),
        new Leg(CM001, ROTTERDAM, destination, new Date(), new Date())
      )
    );

    final RouteCandidateDTO dto = DTOAssembler.toDTO(itinerary);

    assertEquals(2, dto.getLegs().size());
    LegDTO legDTO = dto.getLegs().get(0);
    assertEquals("CM001", legDTO.getVoyageNumber());
    assertEquals("SESTO", legDTO.getFrom());
    assertEquals("CNSHA", legDTO.getTo());

    legDTO = dto.getLegs().get(1);
    assertEquals("CM001", legDTO.getVoyageNumber());
    assertEquals("NLRTM", legDTO.getFrom());
    assertEquals("AUMEL", legDTO.getTo());
  }

  @Test
  public void fromRouteCandidateDTO() throws Exception {
    final List<LegDTO> legs = new ArrayList<LegDTO>();
    legs.add(new LegDTO("CM003", "CNHKG", "USNYC", new Date(), new Date()));
    legs.add(new LegDTO("CM004", "USNYC", "USCHI", new Date(), new Date()));

    final LocationRepository locationRepository = new LocationRepositoryInMem();
    final VoyageRepository voyageRepository = new VoyageRepositoryInMem();


    // Tested call
    final Itinerary itinerary = DTOAssembler.fromDTO(new RouteCandidateDTO(legs), voyageRepository, locationRepository);


    assertNotNull(itinerary);
    assertNotNull(itinerary.legs());
    assertEquals(2, itinerary.legs().size());

    final Leg leg1 = itinerary.legs().get(0);
    assertNotNull(leg1);
    assertEquals(HONGKONG, leg1.loadLocation());
    assertEquals(NEWYORK, leg1.unloadLocation());

    final Leg leg2 = itinerary.legs().get(1);
    assertNotNull(leg2);
    assertEquals(NEWYORK, leg2.loadLocation());
    assertEquals(CHICAGO, leg2.unloadLocation());
  }

  @Test
  public void toDTOList() {
    final List<Location> locationList = Arrays.asList(STOCKHOLM, HAMBURG);

    final List<LocationDTO> dtos = DTOAssembler.toDTOList(locationList);

    assertEquals(2, dtos.size());

    LocationDTO dto = dtos.get(0);
    assertEquals("SESTO", dto.getUnLocode());
    assertEquals("Stockholm", dto.getName());

    dto = dtos.get(1);
    assertEquals("DEHAM", dto.getUnLocode());
    assertEquals("Hamburg", dto.getName());
  }

}
