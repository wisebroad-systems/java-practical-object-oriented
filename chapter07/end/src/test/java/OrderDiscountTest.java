import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderDiscountTest {
    private final MenuItem coffee = new MenuItem("コーヒー", new Money(400));
    private final MenuItem hamburger = new MenuItem("ハンバーグ", new Money(1200));

    private Order orderOf(int yen) {
        Order order = new Order(new DineInFulfillment(3));
        order.addItem(new MenuItem("料理", new Money(yen)), new Quantity(1));
        return order;
    }

    private Order standardOrder() {
        Order order = new Order(new DineInFulfillment(3));
        order.addItem(coffee, new Quantity(2));
        order.addItem(hamburger, new Quantity(1));
        return order;
    }

    /** 呼ばれた回数と渡された金額を記録し、成功を返す支払い方法（テスト用）。 */
    private static class RecordingPayment implements PaymentMethod {
        private int calls = 0;
        private Money paidAmount;

        @Override
        public boolean pay(Money amount) {
            calls++;
            paidAmount = amount;
            return true;
        }
    }

    @Test
    @DisplayName("割引なしでは、支払額は合計と同じ。confirm()も割引なしとして扱う")
    void noDiscountKeepsTotal() {
        Order none = standardOrder();
        none.confirm(new NoDiscountPolicy());
        Order plain = standardOrder();
        plain.confirm();

        assertEquals(new Money(2000), none.getAmountDue());
        assertEquals(new Money(2000), plain.getAmountDue());
    }

    @Test
    @DisplayName("会員割引は明細合計の10％で、1円未満は切り捨てる")
    void memberDiscount() {
        Order order = standardOrder();
        order.confirm(new MemberDiscountPolicy());
        Order small = orderOf(999);
        small.confirm(new MemberDiscountPolicy());

        assertEquals(new Money(1800), order.getAmountDue());
        assertEquals(new Money(900), small.getAmountDue());
    }

    @Test
    @DisplayName("クーポン割引は300円で、明細合計が300円以下なら支払額は0円")
    void couponDiscount() {
        Order order = standardOrder();
        order.confirm(new CouponDiscountPolicy());
        Order exact = orderOf(300);
        exact.confirm(new CouponDiscountPolicy());
        Order small = orderOf(200);
        small.confirm(new CouponDiscountPolicy());

        assertEquals(new Money(1700), order.getAmountDue());
        assertEquals(new Money(0), exact.getAmountDue());
        assertEquals(new Money(0), small.getAmountDue());
    }

    @Test
    @DisplayName("割引しても、合計（totalPrice）は割引前の明細合計のまま")
    void totalPriceStaysBeforeDiscount() {
        Order order = standardOrder();
        order.confirm(new CouponDiscountPolicy());

        assertEquals(new Money(2000), order.totalPrice());
        assertEquals(new Money(1700), order.getAmountDue());
    }

    @Test
    @DisplayName("支払いは、確定時に固定した支払額で依頼する")
    void payUsesAmountDue() {
        Order order = standardOrder();
        order.confirm(new MemberDiscountPolicy());
        RecordingPayment payment = new RecordingPayment();

        assertTrue(order.pay(payment));
        assertEquals(new Money(1800), payment.paidAmount);
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(new Money(1800), order.getAmountDue());
    }

    @Test
    @DisplayName("無料の料理だけの注文は、どの割引でも支払額0円で、支払いを依頼せずにPAIDになる")
    void freeOrderCompletesWithoutPayment() {
        DiscountPolicy[] policies = {
            new NoDiscountPolicy(), new MemberDiscountPolicy(), new CouponDiscountPolicy()
        };
        for (DiscountPolicy policy : policies) {
            Order order = orderOf(0);
            order.confirm(policy);
            RecordingPayment payment = new RecordingPayment();

            assertEquals(new Money(0), order.getAmountDue());
            assertTrue(order.pay(payment));
            assertEquals(OrderStatus.PAID, order.getStatus());
            assertEquals(0, payment.calls);
        }
    }

    @Test
    @DisplayName("確定前は支払額を取得できない")
    void amountDueIsUnavailableBeforeConfirm() {
        assertThrows(IllegalStateException.class, () -> standardOrder().getAmountDue());
    }

    /** 決めておいた割引額を返す割引方針（テスト用）。 */
    private static class FixedDiscountPolicy implements DiscountPolicy {
        private final Money discount;

        FixedDiscountPolicy(Money discount) {
            this.discount = discount;
        }

        @Override
        public Money calculateDiscount(Money totalPrice) {
            return discount;
        }
    }

    @Test
    @DisplayName("割引方針がnullなら確定せず、DRAFTと明細を保ち、後で確定できる")
    void rejectsNullPolicy() {
        Order order = standardOrder();

        assertThrows(IllegalArgumentException.class, () -> order.confirm(null));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertEquals(2, order.getItems().size());
        assertThrows(IllegalStateException.class, order::getAmountDue);

        order.confirm(new MemberDiscountPolicy());
        assertEquals(new Money(1800), order.getAmountDue());
    }

    @Test
    @DisplayName("合計を超える割引額・nullの割引額を返す割引方針では確定しない")
    void rejectsInvalidDiscountFromPolicy() {
        Order over = standardOrder();
        Order nullDiscount = standardOrder();

        assertThrows(
                IllegalArgumentException.class,
                () -> over.confirm(new FixedDiscountPolicy(new Money(2001))));
        assertThrows(
                IllegalArgumentException.class,
                () -> nullDiscount.confirm(new FixedDiscountPolicy(null)));
        assertEquals(OrderStatus.DRAFT, over.getStatus());
        assertEquals(OrderStatus.DRAFT, nullDiscount.getStatus());
        assertEquals(2, over.getItems().size());
    }

    @Test
    @DisplayName("割引額が合計と同じなら、支払額は0円で確定できる")
    void acceptsDiscountEqualToTotal() {
        Order order = standardOrder();
        order.confirm(new FixedDiscountPolicy(new Money(2000)));

        assertEquals(new Money(0), order.getAmountDue());
        assertTrue(order.pay(new RecordingPayment()));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("確定済みの注文は、割引を選び直して再確定できない。状態の違反を先に知らせる")
    void rejectsReconfirmWithDiscount() {
        Order order = standardOrder();
        order.confirm(new MemberDiscountPolicy());

        assertThrows(IllegalStateException.class, () -> order.confirm(new CouponDiscountPolicy()));
        assertThrows(IllegalStateException.class, () -> order.confirm(null));
        assertEquals(new Money(1800), order.getAmountDue());
    }

    @Test
    @DisplayName("空の注文は割引を選んでも確定できない")
    void rejectsEmptyOrder() {
        Order order = new Order(new DineInFulfillment(3));

        assertThrows(IllegalStateException.class, () -> order.confirm(new MemberDiscountPolicy()));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }
}
