package se.citerus.dddsample.domain.shared.experimental;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ValueObjectSupportTest {

    @Test
    public void testEquals() {
        final AValueObject vo1 = new AValueObject("A");
        final AValueObject vo2 = new AValueObject("A");
        final BValueObject vo3 = new BValueObject("A", 1);

        assertThat(vo2).isEqualTo(vo1);
        assertThat(vo1).isEqualTo(vo2);
        assertThat(vo2.equals(vo3)).isFalse();
        assertThat(vo3.equals(vo2)).isFalse();

        assertThat(vo1.sameValueAs(vo2)).isTrue();
        assertThat(vo2.sameValueAs(vo3)).isFalse();
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
