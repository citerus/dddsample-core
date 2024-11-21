package se.citerus.dddsample.interfaces.tracking;

import org.springframework.lang.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validator for {@link TrackCommand}s.
 */
public final class TrackCommandValidator implements Validator {

  public boolean supports(@NonNull final Class<?> clazz) {
    return TrackCommand.class.isAssignableFrom(clazz);
  }

  public void validate(@NonNull final Object object,@NonNull final Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "trackingId", "error.required", "Required");
  }

}

