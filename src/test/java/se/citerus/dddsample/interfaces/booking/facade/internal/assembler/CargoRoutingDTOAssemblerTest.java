package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;

import java.util.Arrays;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;
import static se.citerus.dddsample.domain.model.voyage.SampleVoyages.CM001;

public class CargoRoutingDTOAssemblerTest extends TestCase {

  public void testToDTO() throws Exception {
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

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

    final CargoRoutingDTO dto = assembler.toDTO(cargo);

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

  public void testToDTO_NoItinerary() throws Exception {
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, new Date()));
    final CargoRoutingDTO dto = assembler.toDTO(cargo);

    assertThat(dto.getTrackingId()).isEqualTo("XYZ");
    assertThat(dto.getOrigin()).isEqualTo("SESTO");
    assertThat(dto.getFinalDestination()).isEqualTo("AUMEL");
    assertThat(dto.getLegs().isEmpty()).isTrue();
  }
}
