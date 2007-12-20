package se.citerus.dddsample.web.command;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Validator for {@link se.citerus.dddsample.web.command.TrackCommand}s.
 */
public class TrackCommandValidator implements Validator {

  /**
   * Logger for this class and subclasses
   */
  private final Log logger = LogFactory.getLog(getClass());

  public boolean supports(Class clazz) {
    return TrackCommand.class.isAssignableFrom(clazz);
  }

  public void validate(Object object, Errors errors) {
    final TrackCommand command = (TrackCommand) object;

    if (StringUtils.isEmpty(command.getTrackingId())) {
      final String msg = "Illegal id!";
      logger.warn(msg);
      errors.rejectValue("trackingId", "track.illegal-id", msg);
    }
  }

}

