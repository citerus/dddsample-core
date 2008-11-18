package se.citerus.dddsample.application.ws;

import java.util.Arrays;
import java.util.List;

public class RegistrationFailure extends Exception {
  private final String[] errors;

  RegistrationFailure(final List<String> errors) {
    this.errors = errors.toArray(new String[errors.size()]);
  }

  public String[] getErrors() {
    return errors;
  }

  @Override
  public String getMessage() {
    return "Reistration failure: " + Arrays.toString(errors);
  }
  
}
