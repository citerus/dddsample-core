package se.citerus.dddsample.interfaces.handling.file;

import java.util.List;

public class PartialRegistrationFailureException extends Exception {

  private final List<String> rejectedLines;

  public PartialRegistrationFailureException(List<String> rejectedLines) {
    this.rejectedLines = rejectedLines;
  }

  public List<String> getRejectedLines() {
    return rejectedLines;
  }
  
}
