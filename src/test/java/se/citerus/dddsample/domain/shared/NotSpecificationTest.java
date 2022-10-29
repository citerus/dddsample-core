package se.citerus.dddsample.domain.shared;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class NotSpecificationTest {

  @Test
  public void testAndIsSatisifedBy() {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    NotSpecification<Object> notSpecification = new NotSpecification<Object>(trueSpec);
    assertThat(notSpecification.isSatisfiedBy(new Object())).isFalse();

    notSpecification = new NotSpecification<Object>(falseSpec);
    assertThat(notSpecification.isSatisfiedBy(new Object())).isTrue();

  }
}