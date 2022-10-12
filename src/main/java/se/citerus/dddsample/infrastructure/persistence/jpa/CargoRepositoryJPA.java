package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.CargoDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.CargoDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Hibernate implementation of CargoRepository.
 */
public interface CargoRepositoryJPA extends CrudRepository<CargoDTO, Long>, CargoRepository {

  default Cargo find(TrackingId trackingId) {
    // TODO replace with SQL code in annotation
    Optional<CargoDTO> maybeCargo = StreamSupport.stream(findAll().spliterator(), false)
            .filter(el -> el.trackingId.equalsIgnoreCase(trackingId.idString()))
            .findFirst();
    return maybeCargo.map(CargoDTOConverter::fromDto).orElse(null);
  }

  default void store(final Cargo cargo) {
    save(new CargoDTO());
  }

  default List<Cargo> getAll() {
    return StreamSupport.stream(findAll().spliterator(), false)
            .map(CargoDTOConverter::fromDto)
            .collect(Collectors.toList());
  }

  default TrackingId nextTrackingId() { // TODO replace with SQL code in annotation
    // TODO use an actual DB sequence here, UUID is for in-mem
    final String random = UUID.randomUUID().toString().toUpperCase();
    return new TrackingId(
      random.substring(0, random.indexOf("-"))
    );
  }
}
