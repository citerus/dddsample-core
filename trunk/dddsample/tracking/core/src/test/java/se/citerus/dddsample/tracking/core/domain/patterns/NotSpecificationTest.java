package se.citerus.dddsample.tracking.core.domain.patterns;

import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AlwaysFalseSpec;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AlwaysTrueSpec;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.NotSpecification;

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