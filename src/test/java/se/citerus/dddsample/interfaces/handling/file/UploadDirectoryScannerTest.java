package se.citerus.dddsample.interfaces.handling.file;

import org.apache.commons.codec.Charsets;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.file.PathUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SuppressWarnings("resource")
public class UploadDirectoryScannerTest {

    private static final Date exampleDate = new Date(LocalDateTime.of(2022, 10, 29, 13, 37).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000);
    private File uploadDir;
    private File parseFailureDir;

    @BeforeEach
    void setUp() throws IOException {
        uploadDir = new File(Files.createTempDirectory("upload").toUri());
        parseFailureDir = new File(Files.createTempDirectory("parseFailure").toUri());
    }

    @Test
    public void shouldParseLinesAndPublishEventsForValidFile() throws Exception {
        ArgumentCaptor<HandlingEventRegistrationAttempt> captor = ArgumentCaptor.forClass(HandlingEventRegistrationAttempt.class);
        ApplicationEvents appEventsMock = spy(mock(ApplicationEvents.class));
        UploadDirectoryScanner scanner = new UploadDirectoryScanner(uploadDir, parseFailureDir, appEventsMock);
        URL resource = this.getClass().getResource("/sampleHandlingReportFile.csv");
        assertThat(resource).isNotNull();
        PathUtils.copyFile(resource, uploadDir.toPath().resolve("sampleHandlingReportFile.csv"));

        scanner.run();

        verify(appEventsMock).receivedHandlingEventRegistrationAttempt(captor.capture());
        HandlingEventRegistrationAttempt actual = captor.getValue();
        assertThat(actual).extracting(
                "completionTime",
                "trackingId.id",
                "voyageNumber.number",
                "type",
                "unLocode.unlocode"
        ).contains(exampleDate, "ABC123", "0101", HandlingEvent.Type.CUSTOMS, "SESTO");
        Stream<Path> files = Files.list(uploadDir.toPath());
        assertThat(files.count()).isEqualTo(0);
        files.close();
    }

    @Test
    void shouldCreateFileContainingInvalidLinesIfParsingFails() throws Exception {
        ApplicationEvents appEventsMock = spy(mock(ApplicationEvents.class));
        UploadDirectoryScanner scanner = new UploadDirectoryScanner(uploadDir, parseFailureDir, appEventsMock);
        URL resource = this.getClass().getResource("/sampleInvalidHandlingReportFile.csv");
        assertThat(resource).isNotNull();
        PathUtils.copyFile(resource, uploadDir.toPath().resolve("sampleInvalidHandlingReportFile.csv"));

        scanner.run();

        verifyZeroInteractions(appEventsMock);
        Stream<Path> files = Files.list(parseFailureDir.toPath());
        assertThat(files.count()).isEqualTo(1);
        Path path = Files.list(parseFailureDir.toPath()).collect(Collectors.toList()).get(0);
        String line = FileUtils.readFileToString(new File(path.toUri()), Charsets.UTF_8);
        assertThat(line.trim()).isEqualTo("2022-10-29 13:37    ABC123  0101    XXX   CUSTOMS");
    }
}