package se.citerus.dddsample.domain.shared;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class OrSpecificationTest {

  @Test
  public void testAndIsSatisifedBy() {
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