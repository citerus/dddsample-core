package se.citerus.dddsample.interfaces.booking.facade.internal.assembler;

import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO;

import java.util.ArrayList;
import java.util.List;

public class LocationDTOAssembler {

  public static LocationDTO toDTO(Location location) {
    return new LocationDTO(location.unLocode().idString(), location.name());
  }

  public static List<LocationDTO> toDTOList(List<Location> allLocations) {
    final List<LocationDTO> dtoList = new ArrayList<>(allLocations.size());
    for (Location location : allLocations) {
      dtoList.add(toDTO(location));
    }
    return dtoList;
  }
}
