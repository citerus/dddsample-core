package se.citerus.dddsample.interfaces.booking.facade.internal.assembler

import se.citerus.dddsample.domain.model.location.Location
import se.citerus.dddsample.interfaces.booking.facade.dto.LocationDTO

class LocationDTOAssembler {

  LocationDTO toDTO(Location location) {
    new LocationDTO(location.unLocode().idString(), location.name())
  }

  List<LocationDTO> toDTOList(List<Location> allLocations) {
    allLocations.collect { toDTO(it) }
  }
  
}
