package se.citerus.dddsample.tracking.core.domain.shared;

public class AlwaysTrueSpec extends AbstractSpecification<Object> {
  public boolean isSatisfiedBy(Object o) {
    return true;
  }
}
