package se.citerus.dddsample.tracking.core.domain.patterns.specification;

import junit.framework.TestCase;

public class NotSpecificationTest extends TestCase {

  public void testAndIsSatisifedBy() throws Exception {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    NotSpecification<Object> notSpecification = new NotSpecification<Object>(trueSpec);
    assertFalse(notSpecification.isSatisfiedBy(new Object()));

    notSpecification = new NotSpecification<Object>(falseSpec);
    assertTrue(notSpecification.isSatisfiedBy(new Object()));

  }
}