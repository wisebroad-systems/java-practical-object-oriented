import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class QuantityTest {

    @Test
    @DisplayName("1以上の数量を作れる")
    void createsQuantity() {
        assertEquals(1, new Quantity(1).getValue());
        assertEquals(2, new Quantity(2).getValue());
    }

    @Test
    @DisplayName("0や負の数量は作れない")
    void rejectsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> new Quantity(0));
        assertThrows(IllegalArgumentException.class, () -> new Quantity(-1));
    }

    @Test
    @DisplayName("同じ数量なら、別のインスタンスでも等しく、hashCodeも同じ")
    void equalsByValue() {
        Quantity two = new Quantity(2);
        Quantity sameTwo = new Quantity(2);

        assertNotSame(two, sameTwo);
        assertEquals(two, sameTwo);
        assertEquals(two.hashCode(), sameTwo.hashCode());
        assertNotEquals(new Quantity(3), two);
        assertNotEquals(null, two);
        assertNotEquals(new Money(2), two);
    }

    @Test
    @DisplayName("数量のクラスは継承できない（取得メソッドの上書きでルールを破れない）")
    void isFinal() {
        assertTrue(Modifier.isFinal(Quantity.class.getModifiers()));
    }
}
