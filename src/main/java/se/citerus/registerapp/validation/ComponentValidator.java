package se.citerus.registerapp.validation;

import javax.swing.JComponent;

public interface ComponentValidator {
  boolean validate(JComponent c);
  String getMessage();
}
