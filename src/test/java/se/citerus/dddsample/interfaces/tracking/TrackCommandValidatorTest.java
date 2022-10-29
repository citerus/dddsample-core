package se.citerus.dddsample.interfaces.tracking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import static org.assertj.core.api.Assertions.assertThat;

public class TrackCommandValidatorTest {

  TrackCommandValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new TrackCommandValidator();
  }

  @Test
  public void testValidateIllegalId() {
    TrackCommand command = new TrackCommand();
    BindingResult errors = new BeanPropertyBindingResult(command, "command");
    validator.validate(command, errors);

    assertThat(errors.getErrorCount()).isEqualTo(1);
    FieldError error = errors.getFieldError("trackingId");
    assertThat(error).isNotNull();
    assertThat(error.getRejectedValue()).isNull();
    assertThat(error.getCode()).isEqualTo("error.required");
  }

  @Test
  public void testValidateSuccess() {
    TrackCommand command = new TrackCommand();
    command.setTrackingId("non-empty");
    BindingResult errors = new BeanPropertyBindingResult(command, "command");
    validator.validate(command, errors);

    assertThat(errors.hasErrors()).isFalse();
  }
}
