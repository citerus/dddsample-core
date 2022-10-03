package se.citerus.dddsample;

import com.pathfinder.config.PathfinderApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import se.citerus.dddsample.config.DDDSampleApplicationContext;

@Import({DDDSampleApplicationContext.class,
        PathfinderApplicationContext.class})
@SpringBootApplication
public class Application {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }
}