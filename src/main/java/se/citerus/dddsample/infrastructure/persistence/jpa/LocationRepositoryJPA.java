package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.LocationDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.LocationDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public interface LocationRepositoryJPA extends CrudRepository<LocationDTO, Long>, LocationRepository {

  default Location find(final UnLocode unLocode) {
    return findByUnLoCode(unLocode.idString());
  }

  default Location findByUnLoCode(String unlocode) {
    // TODO replace with SQL code in annotation
    Optional<LocationDTO> maybeLocation = StreamSupport.stream(findAll().spliterator(), false).filter(el -> el.unlocode.equalsIgnoreCase(unlocode)).findFirst();
    return maybeLocation.map(LocationDTOConverter::fromDto).orElse(null);
  }

  @Override
  default List<Location> getAll() {
    return StreamSupport.stream(findAll().spliterator(), false)
            .map(LocationDTOConverter::fromDto)
            .collect(Collectors.toList());
  }

  default Location store(Location location) {
    save(LocationDTOConverter.toDto(location));
    return location;
  }
}
