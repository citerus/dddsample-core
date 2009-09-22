package se.citerus.dddsample.tracking.core.domain.shared;

public class AlwaysFalseSpec extends AbstractSpecification<Object> {
  public boolean isSatisfiedBy(Object o) {
    return false;
  }
}