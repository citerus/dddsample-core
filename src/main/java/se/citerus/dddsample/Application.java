package se.citerus.dddsample;

import com.pathfinder.config.PathfinderApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import se.citerus.dddsample.application.util.SampleDataGenerator;
import se.citerus.dddsample.config.DDDSampleApplicationContext;

import javax.annotation.PostConstruct;

@Configuration
@Import({DDDSampleApplicationContext.class,
        PathfinderApplicationContext.class})
@EnableAutoConfiguration
public class Application {

    @Autowired
    SampleDataGenerator sampleDataGenerator;

    @PostConstruct
    public void init() {
        sampleDataGenerator.generate();
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}