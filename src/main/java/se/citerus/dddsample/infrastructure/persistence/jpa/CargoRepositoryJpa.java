package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.UUID;

/**
 * Hibernate implementation of CargoRepository.
 */
public interface CargoRepositoryJpa extends CrudRepository<Cargo, Long>, CargoRepository {

  Cargo find(TrackingId trackingId);

  default void store(final Cargo cargo) {
    save(cargo);
  }

  default TrackingId nextTrackingId() {
    // TODO use an actual DB sequence here, UUID is for in-mem
    final String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }
}
