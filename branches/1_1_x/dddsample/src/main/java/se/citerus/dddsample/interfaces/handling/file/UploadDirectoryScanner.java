package se.citerus.dddsample.interfaces.handling.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import se.citerus.dddsample.application.ApplicationEvents;
import se.citerus.dddsample.domain.model.cargo.TrackingId;
import se.citerus.dddsample.domain.model.handling.HandlingEvent;
import se.citerus.dddsample.domain.model.location.UnLocode;
import se.citerus.dddsample.domain.model.voyage.VoyageNumber;
import se.citerus.dddsample.interfaces.handling.HandlingEventRegistrationAttempt;
import static se.citerus.dddsample.interfaces.handling.HandlingReportParser.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

/**
 * Periodically scans a certain directory for files and attempts
 * to parse handling event registrations from the contents.
 * <p/>
 * Files that fail to parse are moved into a separate directory,
 * succesful files are deleted.
 */
public class UploadDirectoryScanner extends TimerTask implements InitializingBean {

  private File uploadDirectory;
  private File parseFailureDirectory;

  private final static Log logger = LogFactory.getLog(UploadDirectoryScanner.class);
  private ApplicationEvents applicationEvents;

  @Override
  public void run() {
    for (File file : uploadDirectory.listFiles()) {
      try {
        parse(file);
        delete(file);
        logger.info("Import of " + file.getName() + " complete");
      } catch (Exception e) {
        logger.error(e, e);
        move(file);
      }
    }
  }

  private void parse(final File file) throws IOException {
    final List<String> lines = FileUtils.readLines(file);
    final List<String> rejectedLines = new ArrayList<String>();
    for (String line : lines) {
      try {
        parseLine(line);
      } catch (Exception e) {
        logger.error("Rejected line \n" + line + "\nReason is: " + e);
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
    FileUtils.writeLines(
      new File(parseFailureDirectory, filename), rejectedLines
    );
  }

  private void parseLine(final String line) throws Exception {
    final String[] columns = line.split("\t");
    if (columns.length == 5) {
      queueAttempt(columns[0], columns[1], columns[2], columns[3], columns[4]);
    } else if (columns.length == 4) {
      queueAttempt(columns[0], columns[1], "", columns[2], columns[3]);
    } else {
      throw new IllegalArgumentException("Wrong number of columns on line: " + line + ", must be 4 or 5");
    }
  }

  private void queueAttempt(String completionTimeStr, String trackingIdStr, String voyageNumberStr, String unLocodeStr, String eventTypeStr) throws Exception {
    final List<String> errors = new ArrayList<String>();

    final Date date = parseDate(completionTimeStr, errors);
    final TrackingId trackingId = parseTrackingId(trackingIdStr, errors);
    final VoyageNumber voyageNumber = parseVoyageNumber(voyageNumberStr, errors);
    final HandlingEvent.Type eventType = parseEventType(eventTypeStr, errors);
    final UnLocode unLocode = parseUnLocode(unLocodeStr, errors);

    if (errors.isEmpty()) {
      final HandlingEventRegistrationAttempt attempt = new HandlingEventRegistrationAttempt(new Date(), date, trackingId, voyageNumber, eventType, unLocode);
      applicationEvents.receivedHandlingEventRegistrationAttempt(attempt);
    } else {
      throw new Exception(errors.toString());
    }
  }

  private void delete(final File file) {
    if (!file.delete()) {
      logger.error("Could not delete " + file.getName());
    }
  }

  private void move(final File file) {
    final File destination = new File(parseFailureDirectory, file.getName());
    final boolean result = file.renameTo(destination);
    if (!result) {
      logger.error("Could not move " + file.getName() + " to " + destination.getAbsolutePath());
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (uploadDirectory.equals(parseFailureDirectory)) {
      throw new Exception("Upload and parse failed directories must not be the same directory: " + uploadDirectory);
    }
    if (!uploadDirectory.exists()) {
      uploadDirectory.mkdirs();
    }
    if (!parseFailureDirectory.exists()) {
      parseFailureDirectory.mkdirs();
    }
  }

  public void setUploadDirectory(File uploadDirectory) {
    this.uploadDirectory = uploadDirectory;
  }

  public void setParseFailureDirectory(File parseFailureDirectory) {
    this.parseFailureDirectory = parseFailureDirectory;
  }

  public void setApplicationEvents(ApplicationEvents applicationEvents) {
    this.applicationEvents = applicationEvents;
  }
}
