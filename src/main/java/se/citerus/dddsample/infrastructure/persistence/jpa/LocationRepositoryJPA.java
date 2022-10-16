package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.ReflectionUtils;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.LocationDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface LocationRepositoryJPA extends CrudRepository<LocationDTO, Long>, LocationRepository {

  default Location find(final UnLocode unLocode) {
    LocationDTO locationDTO = findByUnLoCode(unLocode.idString());
    Location location = LocationDTOConverter.fromDto(locationDTO);
    location.setId(locationDTO.id);
    return location;
  }

//  default Location findByUnLoCode(String unlocode) {
//    // TODO replace with SQL code in annotation
//    Optional<LocationDTO> maybeLocation = StreamSupport.stream(findAll().spliterator(), false).filter(el -> el.unlocode.equalsIgnoreCase(unlocode)).findFirst();
//    return maybeLocation.map(LocationDTOConverter::fromDto).orElse(null);
//  }

  @Query("select loc from Location loc where loc.unlocode = :unlocode")
  LocationDTO findByUnLoCode(String unlocode);

  @Override
  default List<Location> getAll() {
    return StreamSupport.stream(findAll().spliterator(), false)
            .map(LocationDTOConverter::fromDto)
            .collect(Collectors.toList());
  }

  default Location store(Location location) {
    LocationDTO dto = save(LocationDTOConverter.toDto(location));
    return location;
  }
}
