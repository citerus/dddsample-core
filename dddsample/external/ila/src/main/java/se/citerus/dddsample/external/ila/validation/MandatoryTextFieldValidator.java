package se.citerus.dddsample.external.ila.validation;

import javax.swing.*;

public class MandatoryTextFieldValidator implements ComponentValidator {
  private String message;

  public MandatoryTextFieldValidator(String message) {
    this.message = message;
  }

  public boolean validate(JComponent c) {
    JTextField field = (JTextField) c;
    
    boolean valid = !"".equals(field.getText());
    
    return valid;
  }

  public String getMessage() {
    return message;
  }

}
