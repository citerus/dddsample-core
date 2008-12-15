package se.citerus.dddsample.interfaces.handling.file;

import java.util.List;

public class PartialRegistrationFailure extends Exception {

  private final List<String> rejectedLines;

  public PartialRegistrationFailure(List<String> rejectedLines) {
    this.rejectedLines = rejectedLines;
  }

  public List<String> getRejectedLines() {
    return rejectedLines;
  }
  
}
