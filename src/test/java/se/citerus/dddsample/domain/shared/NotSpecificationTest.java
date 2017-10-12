package se.citerus.dddsample.domain.shared;

import junit.framework.TestCase;

import static org.assertj.core.api.Assertions.assertThat;

public class NotSpecificationTest extends TestCase {

  public void testAndIsSatisifedBy() throws Exception {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    NotSpecification<Object> notSpecification = new NotSpecification<Object>(trueSpec);
    assertThat(notSpecification.isSatisfiedBy(new Object())).isFalse();

    notSpecification = new NotSpecification<Object>(falseSpec);
    assertThat(notSpecification.isSatisfiedBy(new Object())).isTrue();

  }
}