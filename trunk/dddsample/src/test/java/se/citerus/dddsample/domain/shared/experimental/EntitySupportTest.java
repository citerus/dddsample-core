package se.citerus.dddsample.domain.shared.experimental;

import junit.framework.TestCase;

public class EntitySupportTest extends TestCase {

  public void testOneAnnotationSuccess() {
    OneAnnotationEntity entity = new OneAnnotationEntity("id");
    assertEquals("id", entity.identity());
  }

  public void testSameIdentityEqualsHashcode() {
    OneAnnotationEntity entity1 = new OneAnnotationEntity("A");
    OneAnnotationEntity entity2 = new OneAnnotationEntity("A");
    OneAnnotationEntity entity3 = new OneAnnotationEntity("B");

    assertTrue(entity1.sameAs(entity2));
    assertFalse(entity2.sameAs(entity3));

    assertTrue(entity1.equals(entity2));
    assertFalse(entity2.equals(entity3));

    assertTrue(entity1.hashCode() == entity2.hashCode());
    assertFalse(entity2.hashCode() == entity3.hashCode());
  }

  class OneAnnotationEntity extends EntitySupport<OneAnnotationEntity, String> {
    private final String id;

    OneAnnotationEntity(String id) {
      this.id = id;
    }

    @Override
    public String identity() {
      return id;
    }
  }

}
