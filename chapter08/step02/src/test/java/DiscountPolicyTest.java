import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DiscountPolicyTest {

    @Test
    @DisplayName("割引なしは、どの合計でも0円")
    void noDiscountIsZero() {
        DiscountPolicy policy = new NoDiscountPolicy();

        assertEquals(new Money(0), policy.calculateDiscount(new Money(2000)));
        assertEquals(new Money(0), policy.calculateDiscount(new Money(0)));
    }

    @Test
    @DisplayName("会員割引は合計の10％で、1円未満は切り捨てる")
    void memberDiscountIsTenPercentRoundedDown() {
        DiscountPolicy policy = new MemberDiscountPolicy();

        assertEquals(new Money(200), policy.calculateDiscount(new Money(2000)));
        assertEquals(new Money(99), policy.calculateDiscount(new Money(999)));
        assertEquals(new Money(0), policy.calculateDiscount(new Money(9)));
        assertEquals(new Money(0), policy.calculateDiscount(new Money(0)));
    }

    @Test
    @DisplayName("クーポン割引は300円と合計の小さいほう")
    void couponDiscountIsAtMostTotal() {
        DiscountPolicy policy = new CouponDiscountPolicy();

        assertEquals(new Money(300), policy.calculateDiscount(new Money(2000)));
        assertEquals(new Money(300), policy.calculateDiscount(new Money(300)));
        assertEquals(new Money(200), policy.calculateDiscount(new Money(200)));
        assertEquals(new Money(0), policy.calculateDiscount(new Money(0)));
    }

    @Test
    @DisplayName("どの割引方針も、合計がnullなら拒否する")
    void rejectsNullTotal() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new NoDiscountPolicy().calculateDiscount(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> new MemberDiscountPolicy().calculateDiscount(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> new CouponDiscountPolicy().calculateDiscount(null));
    }
}
