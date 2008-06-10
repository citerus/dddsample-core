package se.citerus.dddsample.domain;

public interface Specification<T> {
  // TODO replace with Specification project dependency
  boolean isSatisfiedBy(T t);
}
