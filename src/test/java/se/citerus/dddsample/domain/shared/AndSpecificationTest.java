package se.citerus.dddsample.domain.shared;

import junit.framework.TestCase;

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