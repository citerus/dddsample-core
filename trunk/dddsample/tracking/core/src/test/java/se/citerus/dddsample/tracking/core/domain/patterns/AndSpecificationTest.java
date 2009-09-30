package se.citerus.dddsample.tracking.core.domain.patterns;

import junit.framework.TestCase;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AlwaysFalseSpec;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AlwaysTrueSpec;
import se.citerus.dddsample.tracking.core.domain.patterns.specification.AndSpecification;

public class AndSpecificationTest extends TestCase {

  public void testAndIsSatisifedBy() throws Exception {
    AlwaysTrueSpec trueSpec = new AlwaysTrueSpec();
    AlwaysFalseSpec falseSpec = new AlwaysFalseSpec();

    AndSpecification<Object> andSpecification = new AndSpecification<Object>(trueSpec, trueSpec);
    assertTrue(andSpecification.isSatisfiedBy(new Object()));

    andSpecification = new AndSpecification<Object>(falseSpec, trueSpec);
    assertFalse(andSpecification.isSatisfiedBy(new Object()));

    andSpecification = new AndSpecification<Object>(trueSpec, falseSpec);
    assertFalse(andSpecification.isSatisfiedBy(new Object()));

    andSpecification = new AndSpecification<Object>(falseSpec, falseSpec);
    assertFalse(andSpecification.isSatisfiedBy(new Object()));

  }
}