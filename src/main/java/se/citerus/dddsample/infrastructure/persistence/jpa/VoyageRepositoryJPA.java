package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
public interface VoyageRepositoryJPA extends CrudRepository<Voyage, Long>, VoyageRepository {

  default Voyage find(final VoyageNumber voyageNumber) {
    return findByVoyageNumber(voyageNumber.idString());
  }

  @Query("select v from Voyage v where v.voyageNumber = :voyageNumber")
  Voyage findByVoyageNumber(String voyageNumber);

  @Override
  default void store(Voyage voyage) {
    save(voyage);
  }
}
