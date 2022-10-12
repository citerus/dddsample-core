package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.repository.CrudRepository;
import se.citerus.dddsample.domain.model.voyage.Voyage;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;
import se.citerus.dddsample.infrastructure.persistence.jpa.converters.VoyageDTOConverter;
import se.citerus.dddsample.infrastructure.persistence.jpa.entities.VoyageDTO;

import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Hibernate implementation of CarrierMovementRepository.
 */
public interface VoyageRepositoryJPA extends CrudRepository<VoyageDTO, Long>, VoyageRepository {

  default Voyage find(final VoyageNumber voyageNumber) {
    return findByVoyageNumber(voyageNumber.idString());
  }

  default Voyage findByVoyageNumber(String voyageNumber) {
    // TODO replace with SQL code in annotation
    Optional<VoyageDTO> maybeVoyage = StreamSupport.stream(findAll().spliterator(), false)
            .filter(el -> el.voyageNumber.equalsIgnoreCase(voyageNumber))
            .findFirst();
    return maybeVoyage.map(VoyageDTOConverter::fromDto).orElse(null);
  }

  @Override
  default void store(Voyage voyage) {
    save(VoyageDTOConverter.toDto(voyage));
  }
}
