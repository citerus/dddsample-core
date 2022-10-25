package se.citerus.dddsample.infrastructure.persistence.jdbi;


import org.jdbi.v3.core.Jdbi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import javax.sql.DataSource;

@Configuration
public class InfrastructurePersistenceConfig {

    @Bean
    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource);
    }

    @Bean
    public HandlingEventRepository handlingEventRepository(Jdbi jdbi) {
        return new HandlingEventRepositoryJdbi(jdbi);
    }

    @Bean
    public CargoRepository cargoRepository(Jdbi jdbi) {
        return new CargoRepositoryJdbi(jdbi);
    }

    @Bean
    public VoyageRepository voyageRepository(Jdbi jdbi) {
        return new VoyageRepositoryJdbi(jdbi);
    }

    @Bean
    public LocationRepository locationRepository(Jdbi jdbi) {
        return new LocationRepositoryJdbi(jdbi);
    }
}
