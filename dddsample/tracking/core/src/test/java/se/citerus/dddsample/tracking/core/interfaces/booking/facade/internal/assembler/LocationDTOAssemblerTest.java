package se.citerus.dddsample.tracking.core.interfaces.booking.facade.internal.assembler;

import junit.framework.TestCase;
import se.citerus.dddsample.tracking.booking.api.dto.LocationDTO;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.HAMBURG;
import static se.citerus.dddsample.tracking.core.domain.model.location.SampleLocations.STOCKHOLM;

import java.util.Arrays;
import java.util.List;

public class LocationDTOAssemblerTest extends TestCase {

  public void testToDTOList() {
    final LocationDTOAssembler assembler = new LocationDTOAssembler();
    final List<Location> locationList = Arrays.asList(STOCKHOLM, HAMBURG);

    final List<LocationDTO> dtos = assembler.toDTOList(locationList);

    assertEquals(2, dtos.size());

    LocationDTO dto = dtos.get(0);
    assertEquals("SESTO", dto.getUnLocode());
    assertEquals("Stockholm", dto.getName());

    dto = dtos.get(1);
    assertEquals("DEHAM", dto.getUnLocode());
    assertEquals("Hamburg", dto.getName());
  }

}
