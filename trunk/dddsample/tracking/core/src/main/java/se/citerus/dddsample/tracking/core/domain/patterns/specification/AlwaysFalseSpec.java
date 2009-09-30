package se.citerus.dddsample.tracking.core.domain.patterns.specification;

public class AlwaysFalseSpec extends AbstractSpecification<Object> {
  public boolean isSatisfiedBy(Object o) {
    return false;
  }
}