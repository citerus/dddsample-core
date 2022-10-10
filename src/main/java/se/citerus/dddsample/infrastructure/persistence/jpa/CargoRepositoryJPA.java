package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.CargoDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CargoDTO;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Hibernate implementation of CargoRepository.
 */
public interface CargoRepositoryJPA extends CrudRepository<CargoDTO, Long>, CargoRepository {

  default Cargo find(TrackingId trackingId) {
    return null; // TODO implement this;
  }

  default void store(final Cargo cargo) {
    save(new CargoDTO());
  }

  default List<Cargo> getAll() {
    return StreamSupport.stream(findAll().spliterator(), false)
            .map(CargoDTOConverter::fromDto)
            .collect(Collectors.toList());
  }

  default TrackingId nextTrackingId() {
    // TODO use an actual DB sequence here, UUID is for in-mem
    final String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }
}
