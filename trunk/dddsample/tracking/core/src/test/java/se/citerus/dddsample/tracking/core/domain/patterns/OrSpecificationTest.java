package se.citerus.dddsample.tracking.core.domain.patterns;

import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AlwaysFalseSpec;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AlwaysTrueSpec;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.OrSpecification;

public class OrSpecificationTest extends TestCase {

  public void testAndIsSatisifedBy() throws Exception {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    OrSpecification<Object> orSpecification = new OrSpecification<Object>(trueSpec, trueSpec);
    assertTrue(orSpecification.isSatisfiedBy(new Object()));

    orSpecification = new OrSpecification<Object>(falseSpec, trueSpec);
    assertTrue(orSpecification.isSatisfiedBy(new Object()));

    orSpecification = new OrSpecification<Object>(trueSpec, falseSpec);
    assertTrue(orSpecification.isSatisfiedBy(new Object()));

    orSpecification = new OrSpecification<Object>(falseSpec, falseSpec);
    assertFalse(orSpecification.isSatisfiedBy(new Object()));

  }
}