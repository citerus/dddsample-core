package se.citerus.dddsample.application.service.dto.assembler;

import junit.framework.TestCase;
import se.citerus.dddsample.application.remoting.dto.CargoRoutingDTO;
import se.citerus.dddsample.application.remoting.dto.LegDTO;
import se.citerus.dddsample.application.remoting.dto.assembler.CargoRoutingDTOAssembler;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.Itinerary;
import se.citerus.dddsample.domain.model.cargo.Leg;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.carrier.CarrierMovement;
import se.citerus.dddsample.domain.model.carrier.CarrierMovementId;
import se.citerus.dddsample.domain.model.location.Location;
import static se.citerus.dddsample.domain.model.location.SampleLocations.*;

import java.util.Arrays;
import java.util.Date;

public class CargoRoutingDTOAssemblerTest extends TestCase {

  public void testToDTO() throws Exception {
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

    final Location origin = STOCKHOLM;
    final Location destination = MELBOURNE;
    final Cargo cargo = new Cargo(new TrackingId("XYZ"), origin, destination);

    final CarrierMovement cm = new CarrierMovement(
      new CarrierMovementId("ABC"), origin, destination, new Date(), new Date());

    final Itinerary itinerary = new Itinerary(
      Arrays.asList(
        new Leg(cm, origin, SHANGHAI),
        new Leg(cm, ROTTERDAM, destination)
      )
    );

    cargo.attachItinerary(itinerary);

    final CargoRoutingDTO dto = assembler.toDTO(cargo);

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

  public void testToDTO_NoItinerary() throws Exception {
    final CargoRoutingDTOAssembler assembler = new CargoRoutingDTOAssembler();

    final Cargo cargo = new Cargo(new TrackingId("XYZ"), STOCKHOLM, MELBOURNE);
    final CargoRoutingDTO dto = assembler.toDTO(cargo);

    assertEquals("XYZ", dto.getTrackingId());
    assertEquals("SESTO", dto.getOrigin());
    assertEquals("AUMEL", dto.getFinalDestination());
    assertTrue(dto.getLegs().isEmpty());
  }
}
