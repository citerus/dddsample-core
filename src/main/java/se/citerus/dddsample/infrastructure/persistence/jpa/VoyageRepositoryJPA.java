package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.VoyageDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.VoyageDTO;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
public interface VoyageRepositoryJPA extends CrudRepository<VoyageDTO, Long>, VoyageRepository {

  default Voyage find(final VoyageNumber voyageNumber) {
    return findByVoyageNumber(voyageNumber.idString());
  }

  default Voyage findByVoyageNumber(String voyageNumber) {
    return null; // TODO implement this
  }

  @Override
  default void store(Voyage voyage) {
    save(VoyageDTOConverter.toDto(voyage));
  }
}
