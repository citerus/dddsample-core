package se.citerus.dddsample.interfaces.tracking;

import junit.framework.TestCase;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class TrackCommandValidatorTest extends TestCase {

  TrackCommandValidator validator;
  TrackCommand command;
  BindingResult errors;

  protected void setUp() throws Exception {
    validator = new TrackCommandValidator();
    command = new TrackCommand();
    errors = new BeanPropertyBindingResult(command, "command");
  }

  public void testValidateIllegalId() throws Exception {
    validator.validate(command, errors);

    assertEquals(1, errors.getErrorCount());
    FieldError error = errors.getFieldError("trackingId");
    assertNotNull(error);
    assertNull(error.getRejectedValue());
    assertEquals("error.required", error.getCode());
  }
    
  public void testValidateSuccess() throws Exception {
    command.setTrackingId("non-empty");
    validator.validate(command, errors);

    assertFalse(errors.hasErrors());
  }

}
