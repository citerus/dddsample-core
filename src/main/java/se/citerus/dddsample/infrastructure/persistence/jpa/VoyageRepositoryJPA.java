package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.Query;
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
    VoyageDTO voyageDTO = findByVoyageNumber(voyageNumber.idString());
    return VoyageDTOConverter.fromDto(voyageDTO);
  }

//  default Voyage findByVoyageNumber(String voyageNumber) {
//    // TODO replace with SQL code in annotation
//    Optional<VoyageDTO> maybeVoyage = StreamSupport.stream(findAll().spliterator(), false)
//            .filter(el -> el.voyageNumber.equalsIgnoreCase(voyageNumber))
//            .findFirst();
//    return maybeVoyage.map(VoyageDTOConverter::fromDto).orElse(null);
//  }

  @Query("select v from Voyage v where v.voyageNumber = :voyageNumber")
  VoyageDTO findByVoyageNumber(String voyageNumber);

  @Override
  default void store(Voyage voyage) {
    save(VoyageDTOConverter.toDto(voyage));
  }
}
