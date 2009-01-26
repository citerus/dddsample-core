package se.citerus.dddsample.interfaces.handling.ws;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class HandlingReportErrors extends Exception {

  @XmlElement
  List<String> errors;

  public HandlingReportErrors() {
  }

  public HandlingReportErrors(List<String> errors) {
    setErrors(errors);
  }

  public List<String> getErrors() {
    return errors;
  }

  public void setErrors(List<String> errors) {
    if (errors == null) {
      errors = new ArrayList<String>();
    }
    this.errors = errors;
  }

  @Override
  @XmlElement(required = true)
  public String getMessage() {
    return "Report errors: " + errors;
  }
  
}
