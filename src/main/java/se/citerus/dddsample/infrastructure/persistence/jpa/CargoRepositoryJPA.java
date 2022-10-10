package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.cargo.Cargo;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.cargo.TrackingId;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Hibernate implementation of CargoRepository.
 */
public interface CargoRepositoryJPA extends CrudRepository<Cargo, Long>, CargoRepository {

  default Cargo find(TrackingId trackingId) {
    return findByTrackingId(trackingId.idString());
  }

  @Query("select c from Cargo c where c.trackingId = :trackingId")
  Cargo findByTrackingId(String trackingId);

  default void store(final Cargo cargo) {
    save(cargo);
  }

  default List<Cargo> getAll() {
    return StreamSupport.stream(findAll().spliterator(), false)
            .collect(Collectors.toList());
  }

  @Query(value = "SELECT UPPER(SUBSTR(CAST(UUID() AS VARCHAR(38)), 0, 9)) AS id FROM (VALUES(0))", nativeQuery = true)
  String nextTrackingIdString();

  default TrackingId nextTrackingId() {
    return new TrackingId(nextTrackingIdString());
  }
}
