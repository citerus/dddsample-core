package se.citerus.registerapp.validation;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;

import junit.framework.TestCase;

public class ValidationTest extends TestCase {
  private boolean actionPerformed;

  public void testValidationScenario() throws Exception {
    JFrame frame = new JFrame();
    
    final FormValidationDecorator validationDecorator = new FormValidationSwingDecorator(frame);
    
    JTextField field1 = new JTextField();
    JTextField field2 = new JTextField();
    JButton button = new JButton();
    frame.add(field1);
    frame.add(field2);
    frame.add(button);
    
    
    ValidatorMock validator1 = createValidator("No Way!", true);
    validationDecorator.add(field1, validator1);
    
    ValidatorMock validator2 = createValidator("My Way!", true);
    validationDecorator.add(field2, validator2);
    
   button.addActionListener(new ActionListener(){
    public void actionPerformed(ActionEvent e) {
      if (validationDecorator.validate()){
        onAction();
      }
    }
   });
   
   
   
   field1.setText("Field1");
   field2.setText("");
   button.doClick();
   assertTrue("Field1 not validated", validator1.isValidated());
   assertTrue("Field2 not validated", validator2.isValidated());
   assertTrue("Button action not performed", actionPerformed);
  }
  
  public void testValidationFailure() throws Exception {
    JFrame frame = new JFrame();
    final FormValidationDecorator validator = new FormValidationSwingDecorator(frame);
    
    JTextField field1 = new JTextField();
    JTextField field2 = new JTextField();
    JButton button = new JButton();
    frame.add(field1);
    frame.add(field2);
    frame.add(button);
    
    ValidatorMock validator1 = createValidator("No Way!", true);
    validator.add(field1, validator1);
    
    ValidatorMock validator2 = createValidator("My Way!", false);
    validator.add(field2, validator2);
    
   button.addActionListener(new ActionListener(){
    public void actionPerformed(ActionEvent e) {
      if (validator.validate()){
        onAction();
      }
    }
   });
   
   field1.setText("Field1");
   field2.setText("");
   button.doClick();
   assertTrue("Field1 not validated", validator1.isValidated());
   assertTrue("Field2 not validated", validator2.isValidated());
   assertFalse("Button action should not be performed", actionPerformed);
  }
  

  private ValidatorMock createValidator(String message, boolean result) {
    return new ValidatorMock(message, result);
  }

  protected void onAction() {
    this.actionPerformed = true;
    // Do some action...
  }
  
  private static final class ValidatorMock implements ComponentValidator{
    private final boolean result;
    private boolean validated;
    private String message;
    
    public ValidatorMock(String message, boolean result) {
      this.message = message;
      this.result = result;
    }

    public boolean validate(JComponent c) {
      validated = true;
      return result;
    }

    public boolean isValidated() {
      return validated;
    }

    public String getMessage() {
      return message;
    }
    
  }
}
