package se.citerus.registerapp.validation;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JComponent;

public abstract class AbstractFormValidationDecorator implements FormValidationDecorator{
  private HashMap<JComponent, ComponentValidator> validators = new HashMap<JComponent, ComponentValidator>();

  public void add(final JComponent component, ComponentValidator validator) {
    this.validators.put(component, validator);

    component.addKeyListener(new KeyListener(){
      public void keyPressed(KeyEvent e) {
        for (final Entry<JComponent, ComponentValidator> entry : validators.entrySet()) {
          undecorate(entry.getKey());
        }
      }

      public void keyReleased(KeyEvent e) {}
      public void keyTyped(KeyEvent e) {}
    });
  }

  public boolean validate() {
    boolean allValidated = true;
    for (final Entry<JComponent, ComponentValidator> entry : validators.entrySet()) {
      if (!entry.getValue().validate(entry.getKey())){
        decorate(entry.getKey(), entry.getValue());
        allValidated = false;
      }
    }
    return allValidated;
  }
  
  public abstract void decorate(JComponent component, ComponentValidator validator);
  public abstract void undecorate(JComponent component);
}