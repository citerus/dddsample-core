package se.citerus.dddsample.domain.shared;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class OrSpecificationTest extends TestCase {

  public void testAndIsSatisifedBy() throws Exception {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    OrSpecification<Object> orSpecification = new OrSpecification<Object>(trueSpec, trueSpec);
    assertThat(orSpecification.isSatisfiedBy(new Object())).isTrue();

    orSpecification = new OrSpecification<Object>(falseSpec, trueSpec);
    assertThat(orSpecification.isSatisfiedBy(new Object())).isTrue();

    orSpecification = new OrSpecification<Object>(trueSpec, falseSpec);
    assertThat(orSpecification.isSatisfiedBy(new Object())).isTrue();

    orSpecification = new OrSpecification<Object>(falseSpec, falseSpec);
    assertThat(orSpecification.isSatisfiedBy(new Object())).isFalse();

  }
}