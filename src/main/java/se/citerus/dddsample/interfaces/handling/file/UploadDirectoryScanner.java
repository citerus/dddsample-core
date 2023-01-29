package se.citerus.dddsample.interfaces.handling.file;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static se.citerus.dddsample.interfaces.handling.HandlingReportParser.*;

/**
 * Periodically scans a certain directory for files and attempts
 * to parse handling event registrations from the contents.
 * <p/>
 * Files that fail to parse are moved into a separate directory,
 * successful files are deleted.
 */
public class UploadDirectoryScanner extends TimerTask implements InitializingBean {

  private final File uploadDirectory;
  private final File parseFailureDirectory;

  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final ApplicationEvents applicationEvents;

  public UploadDirectoryScanner(@NonNull File uploadDirectory, @NonNull File parseFailureDirectory, ApplicationEvents applicationEvents) {
    this.uploadDirectory = uploadDirectory;
    this.parseFailureDirectory = parseFailureDirectory;
    this.applicationEvents = applicationEvents;
  }

  @SuppressWarnings("ConstantConditions")
  @Override
  public void run() {
    for (File file : uploadDirectory.listFiles()) {
      try {
        parse(file);
        delete(file);
        logger.info("Import of {} complete", file.getName());
      } catch (Exception e) {
        logger.error("Error parsing uploaded file", e);
        move(file);
      }
    }
  }

  /**
   * Reads an uploaded file into memory and parses it line by line, returning a list of parsed lines.
   * Any unparseable lines will be stored in a new file and saved to the parseFailureDirectory.
   * @param file the file to parse.
   * @throws IOException if reading or writing the file fails.
   */
  private void parse(final File file) throws IOException {
    final List<String> lines = Files.readAllLines(file.toPath());
    final List<String> rejectedLines = new ArrayList<>();
    for (String line : lines) {
      try {
        String[] columns = parseLine(line);
        queueAttempt(columns[0], columns[1], columns[2], columns[3], columns[4]);
      } catch (Exception e) {
        logger.error("Rejected line: {}", line, e);
        rejectedLines.add(line);
      }
    }
    if (!rejectedLines.isEmpty()) {
      writeRejectedLinesToFile(toRejectedFilename(file), rejectedLines);
    }
  }

  private String toRejectedFilename(final File file) {
    return file.getName() + ".reject";
  }

  private void writeRejectedLinesToFile(final String filename, final List<String> rejectedLines) throws IOException {
    Files.write(
            new File(parseFailureDirectory, filename).toPath(),
            rejectedLines,
            StandardOpenOption.APPEND);
  }

  private String[] parseLine(final String line) {
    final String[] columns = line.split("\\s{2,}");
    if (columns.length == 5) {
      return new String[]{columns[0], columns[1], columns[2], columns[3], columns[4]};
    } else if (columns.length == 4) {
      return new String[]{columns[0], columns[1], "", columns[2], columns[3]};
    } else {
      throw new IllegalArgumentException(String.format("Wrong number of columns on line: %s, must be 4 or 5", line));
    }
  }

  private void queueAttempt(String completionTimeStr, String trackingIdStr, String voyageNumberStr, String unLocodeStr, String eventTypeStr) throws Exception {
    try {
      final Date date = parseDate(completionTimeStr);
      final TrackingId trackingId = parseTrackingId(trackingIdStr);
      final VoyageNumber voyageNumber = parseVoyageNumber(voyageNumberStr);
      final HandlingEvent.Type eventType = parseEventType(eventTypeStr);
      final UnLocode unLocode = parseUnLocode(unLocodeStr);
      final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(
              new Date(), date, trackingId, voyageNumber, eventType, unLocode);
      applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
    } catch (IllegalArgumentException e) {
      throw new Exception("Error parsing HandlingReport", e);
    }
  }

  private void delete(final File file) {
    if (!file.delete()) {
      logger.error("Could not delete file: {}", file.getName());
    }
  }

  private void move(final File file) {
    final File destination = new File(parseFailureDirectory, file.getName());
    final boolean result = file.renameTo(destination);
    if (!result) {
      logger.error("Could not move {} to {}", file.getName(), destination.getAbsolutePath());
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (uploadDirectory.equals(parseFailureDirectory)) {
      throw new Exception(String.format("Upload and parse failed directories must not be the same directory: %s", uploadDirectory));
    }
    for (File dir : Arrays.asList(uploadDirectory, parseFailureDirectory)) {
      if (!(dir.exists() || dir.mkdirs())) {
        throw new IllegalStateException("Failed to create dir: " + dir);
      }
    }
  }
}
