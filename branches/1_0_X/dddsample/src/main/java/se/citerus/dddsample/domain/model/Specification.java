package se.citerus.dddsample.domain.model;

public interface Specification<T> {
  // TODO replace with Specification project dependency
  boolean isSatisfiedBy(T t);
}
