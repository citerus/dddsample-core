package se.citerus.dddsample.tracking.core.interfaces.booking.facade.internal.assembler;

import se.citerus.dddsample.tracking.booking.api.dto.LocationDTO;
import se.citerus.dddsample.tracking.core.domain.model.location.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationDTOAssembler {

  public LocationDTO toDTO(Location location) {
    return new LocationDTO(location.unLocode().stringValue(), location.name());
  }

  public List<LocationDTO> toDTOList(List<Location> allLocations) {
    final List<LocationDTO> dtoList = new ArrayList<LocationDTO>(allLocations.size());
    for (Location location : allLocations) {
      dtoList.add(toDTO(location));
    }
    return dtoList;
  }
}
