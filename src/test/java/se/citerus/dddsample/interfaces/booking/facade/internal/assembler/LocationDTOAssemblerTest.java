package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import junit.framework.TestCase;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static se.citerus.dddsample.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.domain.model.location.SampleLocations.STOCKHOLM;

public class LocationDTOAssemblerTest extends TestCase {

  public void testToDTOList() {
    final LocationDTOAssembler assembler = new LocationDTOAssembler();
    final List<Location> locationList = Arrays.asList(STOCKHOLM, HAMBURG);

    final List<LocationDTO> dtos = assembler.toDTOList(locationList);

    assertThat(dtos).hasSize(2);

    LocationDTO dto = dtos.get(0);
    assertThat(dto.getUnLocode()).isEqualTo("SESTO");
    assertThat(dto.getName()).isEqualTo("Stockholm");

    dto = dtos.get(1);
    assertThat(dto.getUnLocode()).isEqualTo("DEHAM");
    assertThat(dto.getName()).isEqualTo("Hamburg");
  }

}
