package se.citerus.dddsample.infrastructure.persistence.jpa;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import se.citerus.dddsample.interfaces.handling.file.UploadDirectoryScanner;

/**
 * This config is required by the repository tests to avoid a strange behavior where the UploadDirectoryScanner
 * creates directories despite the file paths not having been initialized properly.
 */
@TestConfiguration
public class TestRepositoryConfig {
    @Primary
    @Bean
    public UploadDirectoryScanner uploadDirectoryScanner() {
        return new UploadDirectoryScanner(null, null, null) {
            @Override
            public void afterPropertiesSet() {
                // noop
            }
        };
    }
}
