package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.location.Location;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.location.UnLocode;

public interface LocationRepositoryJpa extends CrudRepository<Location, Long>, LocationRepository {

  default Location find(final UnLocode unLocode) {
    return findByUnLoCode(unLocode.idString());
  }

  Location findByUnLoCode(String unlocode);

  default Location store(Location location) {
    return save(location);
  }
}
