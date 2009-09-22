package se.citerus.dddsample.external.ila.validation;

import javax.swing.*;


public interface FormValidationDecorator {

  void add(final JComponent component, ComponentValidator validator);

  boolean validate();

  
}
