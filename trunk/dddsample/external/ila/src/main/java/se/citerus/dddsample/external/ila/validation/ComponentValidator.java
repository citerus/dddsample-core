package se.citerus.dddsample.external.ila.validation;

import javax.swing.*;

public interface ComponentValidator {
  boolean validate(JComponent c);
  String getMessage();
}
