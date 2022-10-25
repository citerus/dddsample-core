package se.citerus.dddsample;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;

@TestConfiguration
public class TestInfrastructurePersistenceConfig {

    @Bean // TODO this should be created from application.properties. Figure out why it isn't.
    public DataSource dataSource() {
        HikariConfig configuration = new HikariConfig();
        configuration.setJdbcUrl("jdbc:hsqldb:mem:dddsample_test");
        configuration.setUsername("sa");
        configuration.setPassword("");
        return new HikariDataSource(configuration);
    }

    // Utility method to truncate all the data in the test database.
    public static void truncateAllTables(Jdbi jdbi) {
        jdbi.useHandle(h -> {
            h.createUpdate("TRUNCATE TABLE Cargo").execute();
            h.createUpdate("TRUNCATE TABLE Leg").execute();
            h.createUpdate("TRUNCATE TABLE Voyage").execute();
            h.createUpdate("TRUNCATE TABLE CarrierMovement").execute();
            h.createUpdate("TRUNCATE TABLE HandlingEvent").execute();
            h.createUpdate("TRUNCATE TABLE RouteSpecification").execute();
            h.createUpdate("TRUNCATE TABLE Location").execute();
        });
    }
}
