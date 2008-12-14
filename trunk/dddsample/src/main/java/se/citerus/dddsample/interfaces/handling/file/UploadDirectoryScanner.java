package se.citerus.dddsample.interfaces.handling.file;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import se.citerus.dddsample.interfaces.handling.RegistrationFailure;
import se.citerus.dddsample.interfaces.handling.RegistrationParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Periodically scans a certain directory for files and attempts
 * to parse handling event registrations from the contents.
 *
 * Files that fail to parse are moved into a separate directory,
 * succesful files are deleted.
 */
public class UploadDirectoryScanner extends TimerTask implements InitializingBean {

  private File uploadDirectory;
  private File parseFailureDirectory;
  private RegistrationParser registrationParser;

  private final static Log logger = LogFactory.getLog(UploadDirectoryScanner.class);

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

  private void parse(final File file) throws IOException, RegistrationFailure {
    final List<String> lines = FileUtils.readLines(file);
    final List<String> rejectedLines = new ArrayList<String>();
    for (String line : lines) {
      try {
        parseLine(line);
      } catch (Exception e) {
        logger.error("Rejected line '" + line + "'. Reason is: " + e, e);
        rejectedLines.add(line);
      }
    }
    if (!rejectedLines.isEmpty()) {
      writeRejectedLinesToFile(toRejectedFilename(file), rejectedLines);
    }
  }

  private String toRejectedFilename(File file) {
    return file.getName() + ".reject";
  }

  private void writeRejectedLinesToFile(String filename, List<String> rejectedLines) throws IOException {
    FileUtils.writeLines(
        new File(parseFailureDirectory, filename), rejectedLines
    );
  }

  private void parseLine(final String line) throws RegistrationFailure {
    final String[] columns = line.split("\t");
    if (columns.length == 5) {
      registrationParser.convertAndSend(
        columns[0],
        columns[1],
        columns[2],
        columns[3],
        columns[4]
      );
    } else if (columns.length == 4) {
      registrationParser.convertAndSend(
        columns[0],
        columns[1],
        "",
        columns[2],
        columns[3]
      );
    } else {
      throw new IllegalArgumentException("Format error on line: " + line);
    }
  }

  private void delete(File file) {
    if (!file.delete()) {
      logger.error("Could not delete " + file.getName());  
    }
  }

  private void move(File file) {
    final File destination = new File(parseFailureDirectory, file.getName());
    final boolean result = file.renameTo(destination);
    if (!result) {
      logger.error("Could not move " + file.getName() + " to " + destination.getAbsolutePath());
    }
  }

  public void setUploadDirectory(File uploadDirectory) {
    this.uploadDirectory = uploadDirectory;
  }

  public void setParseFailureDirectory(File parseFailureDirectory) {
    this.parseFailureDirectory = parseFailureDirectory;
  }

  public void setRegistrationParser(RegistrationParser registrationParser) {
    this.registrationParser = registrationParser;
  }

  public void afterPropertiesSet() throws Exception {
    if (uploadDirectory.equals(parseFailureDirectory)) {
      throw new Exception("Upload and parse failed directories must not be the same directory: " + uploadDirectory);
    }
  }
}
