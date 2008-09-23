package se.citerus.dddsample.domain.model;

public interface Specification<T> {

  boolean isSatisfiedBy(T t);
    
}
