package se.citerus.registerapp.validation;

import javax.swing.JComponent;


public interface FormValidationDecorator {

  void add(final JComponent component, ComponentValidator validator);

  boolean validate();

  
}
