package se.citerus.dddsample.interfaces.tracking;

import junit.framework.TestCase;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

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

    assertThat(errors.getErrorCount()).isEqualTo(1);
    FieldError error = errors.getFieldError("trackingId");
    assertThat(error).isNotNull();
    assertThat(error.getRejectedValue()).isNull();
    assertThat(error.getCode()).isEqualTo("error.required");
  }
    
  public void testValidateSuccess() throws Exception {
    command.setTrackingId("non-empty");
    validator.validate(command, errors);

    assertThat(errors.hasErrors()).isFalse();
  }

}
