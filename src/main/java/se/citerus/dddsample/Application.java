package se.citerus.dddsample;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.transaction.PlatformTransactionManager;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.domain.model.cargo.CargoRepository;
import se.citerus.dddsample.domain.model.handling.HandlingEventRepository;
import se.citerus.dddsample.domain.model.location.LocationRepository;
import se.citerus.dddsample.domain.model.voyage.VoyageRepository;

import javax.annotation.PostConstruct;

@Configuration
@ImportResource({
        "classpath:/com/pathfinder/internal/applicationContext.xml",
        "classpath:context-infrastructure.xml",
        "classpath:context-application.xml",
        "classpath:context-domain.xml",
        "classpath:context-interfaces.xml"})
@EnableAutoConfiguration
public class Application {

    @Autowired
    PlatformTransactionManager platformTransactionManager;

    @Autowired
    SessionFactory sessionFactory;

    @Autowired
    CargoRepository cargoRepository;

    @Autowired
    VoyageRepository voyageRepository;

    @Autowired
    LocationRepository locationRepository;

    @Autowired
    HandlingEventRepository handlingEventRepository;

    @Bean
    public SampleDataGenerator sampleDataGenerator() {
        return new SampleDataGenerator(platformTransactionManager, sessionFactory, cargoRepository, voyageRepository, locationRepository, handlingEventRepository);
    }

    @PostConstruct
    public void init() {
        sampleDataGenerator().generate();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}