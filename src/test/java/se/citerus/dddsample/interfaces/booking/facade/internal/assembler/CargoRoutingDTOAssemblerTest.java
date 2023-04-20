package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import org.junit.jupiter.api.Test;
import se.citerus.dddsample.domain.model.cargo.*;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.dto.CargoRoutingDTO;
import se.citerus.dddsample.interfaces.booking.facade.dto.LegDTO;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.infrastructure.sampledata.SampleLocations.*;
import static se.citerus.dddsample.infrastructure.sampledata.SampleVoyages.CM001;

public class CargoRoutingDTOAssemblerTest {

  @Test
  public void testToDTO() {
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(origin, destination, Instant.now()));

    final Itinerary itinerary = new Itinerary(
      List.of(
        new Leg(CM001, origin, SHANGHAI, Instant.now(), Instant.now()),
        new Leg(CM001, ROTTERDAM, destination, Instant.now(), Instant.now())
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

  @Test
  public void testToDTO_NoItinerary() {
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

    final Cargo cargo = new Cargo(new TrackingId("XYZ"), new RouteSpecification(STOCKHOLM, MELBOURNE, Instant.now()));
    final CargoRoutingDTO dto = assembler.toDTO(cargo);

    assertThat(dto.getTrackingId()).isEqualTo("XYZ");
    assertThat(dto.getOrigin()).isEqualTo("SESTO");
    assertThat(dto.getFinalDestination()).isEqualTo("AUMEL");
    assertThat(dto.getLegs().isEmpty()).isTrue();
  }
}
