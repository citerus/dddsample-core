package se.citerus.dddsample.domain.shared;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class AndSpecificationTest extends TestCase {

  public void testAndIsSatisifedBy() throws Exception {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    AndSpecification<Object> andSpecification = new AndSpecification<Object>(trueSpec, trueSpec);
    assertThat(andSpecification.isSatisfiedBy(new Object())).isTrue();

    andSpecification = new AndSpecification<Object>(falseSpec, trueSpec);
    assertThat(andSpecification.isSatisfiedBy(new Object())).isFalse();

    andSpecification = new AndSpecification<Object>(trueSpec, falseSpec);
    assertThat(andSpecification.isSatisfiedBy(new Object())).isFalse();

    andSpecification = new AndSpecification<Object>(falseSpec, falseSpec);
    assertThat(andSpecification.isSatisfiedBy(new Object())).isFalse();

  }
}