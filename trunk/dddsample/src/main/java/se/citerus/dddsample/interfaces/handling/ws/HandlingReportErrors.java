package se.citerus.dddsample.interfaces.handling.ws;

import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.List;

public class HandlingReportErrors extends Exception {

  private String[] errors;

  public HandlingReportErrors() {
  }

  public HandlingReportErrors(final List<String> errors) {
    this.errors = errors.toArray(new String[errors.size()]);
  }

  @XmlElement(required = true)
  public String[] getErrors() {
    return errors;
  }

  public void setErrors(String[] errors) {
    this.errors = errors;
  }


  @Override
  @XmlElement(required = true)
  public String getMessage() {
    return "Reistration failure: " + Arrays.toString(errors);
  }
  
}
