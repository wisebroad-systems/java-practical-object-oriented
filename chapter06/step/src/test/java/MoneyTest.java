import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    @DisplayName("0円以上の金額を作れる")
    void createsMoney() {
        assertEquals(0, new Money(0).getYen());
        assertEquals(400, new Money(400).getYen());
    }

    @Test
    @DisplayName("負の金額は作れない")
    void rejectsNegativeAmount() {
        assertThrows(IllegalArgumentException.class, () -> new Money(-1));
    }

    @Test
    @DisplayName("同じ円金額なら、別のインスタンスでも等しく、hashCodeも同じ")
    void equalsByAmount() {
        Money price = new Money(400);
        Money samePrice = new Money(400);

        assertNotSame(price, samePrice);
        assertEquals(price, samePrice);
        assertEquals(price.hashCode(), samePrice.hashCode());
        assertNotEquals(new Money(450), price);
        assertNotEquals(null, price);
        assertNotEquals("400", price);
    }

    @Test
    @DisplayName("加算は新しい金額を返し、元の金額は変わらない")
    void addReturnsNewMoney() {
        Money price = new Money(400);

        assertEquals(new Money(1600), price.add(new Money(1200)));
        assertEquals(new Money(400), price);
    }

    @Test
    @DisplayName("数量との乗算は新しい金額を返し、元の金額は変わらない")
    void multiplyReturnsNewMoney() {
        Money price = new Money(400);

        assertEquals(new Money(800), price.multiply(2));
        assertEquals(new Money(400), price);
    }

    @Test
    @DisplayName("加える金額がnull、掛ける数量が1未満なら拒否する")
    void rejectsInvalidArguments() {
        Money price = new Money(400);

        assertThrows(IllegalArgumentException.class, () -> price.add(null));
        assertThrows(IllegalArgumentException.class, () -> price.multiply(0));
        assertThrows(IllegalArgumentException.class, () -> price.multiply(-1));
    }

    @Test
    @DisplayName("intの範囲を超える計算はArithmeticExceptionで知らせる")
    void detectsOverflow() {
        Money max = new Money(Integer.MAX_VALUE);

        assertThrows(ArithmeticException.class, () -> max.add(new Money(1)));
        assertThrows(ArithmeticException.class, () -> max.multiply(2));
    }

    @Test
    @DisplayName("金額のクラスは継承できない（取得メソッドの上書きでルールを破れない）")
    void isFinal() {
        assertTrue(Modifier.isFinal(Money.class.getModifiers()));
    }
}
