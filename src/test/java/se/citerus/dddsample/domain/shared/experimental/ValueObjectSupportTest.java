package se.citerus.dddsample.domain.shared.experimental;

import junit.framework.TestCase;


public class ValueObjectSupportTest extends TestCase {

    public void testEquals() {
        final AValueObject vo1 = new AValueObject("A");
        final AValueObject vo2 = new AValueObject("A");
        final BValueObject vo3 = new BValueObject("A", 1);

        assertEquals(vo1, vo2);
        assertEquals(vo2, vo1);
        assertFalse(vo2.equals(vo3));
        assertFalse(vo3.equals(vo2));

        assertTrue(vo1.sameValueAs(vo2));
        assertFalse(vo2.sameValueAs(vo3));
    }

    class AValueObject extends ValueObjectSupport<AValueObject> {
        String s;

        AValueObject(String s) {
            this.s = s;
        }

        AValueObject() {
        }
    }

    class BValueObject extends AValueObject {
        int x;

        BValueObject(String s, int x) {
            super(s);
            this.x = x;
        }

    }

}
