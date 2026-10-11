import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderDiscountTest {
    private final MenuItem coffee = new MenuItem("コーヒー", new Money(400));
    private final MenuItem hamburger = new MenuItem("ハンバーグ", new Money(1200));

    private Order orderOf(int yen) {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("料理", new Money(yen)), new Quantity(1));
        return order;
    }

    private Order standardOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
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
    @DisplayName("割引なしでは、支払額は合計と同じ")
    void noDiscountKeepsTotal() {
        Order none = standardOrder();
        none.confirmContent();
        none.fixAmountDue(new NoDiscountPolicy());

        assertEquals(new Money(2000), none.getAmountDue());
    }

    @Test
    @DisplayName("会員割引は明細合計の10％で、1円未満は切り捨てる")
    void memberDiscount() {
        Order order = standardOrder();
        order.confirmContent();
        order.fixAmountDue(new MemberDiscountPolicy());
        Order small = orderOf(999);
        small.confirmContent();
        small.fixAmountDue(new MemberDiscountPolicy());

        assertEquals(new Money(1800), order.getAmountDue());
        assertEquals(new Money(900), small.getAmountDue());
    }

    @Test
    @DisplayName("クーポン割引は300円で、明細合計が300円以下なら支払額は0円")
    void couponDiscount() {
        Order order = standardOrder();
        order.confirmContent();
        order.fixAmountDue(new CouponDiscountPolicy());
        Order exact = orderOf(300);
        exact.confirmContent();
        exact.fixAmountDue(new CouponDiscountPolicy());
        Order small = orderOf(200);
        small.confirmContent();
        small.fixAmountDue(new CouponDiscountPolicy());

        assertEquals(new Money(1700), order.getAmountDue());
        assertEquals(new Money(0), exact.getAmountDue());
        assertEquals(new Money(0), small.getAmountDue());
    }

    @Test
    @DisplayName("割引しても、合計（totalPrice）は割引前の明細合計のまま")
    void totalPriceStaysBeforeDiscount() {
        Order order = standardOrder();
        order.confirmContent();
        order.fixAmountDue(new CouponDiscountPolicy());

        assertEquals(new Money(2000), order.totalPrice());
        assertEquals(new Money(1700), order.getAmountDue());
    }

    @Test
    @DisplayName("支払いは、確定時に固定した支払額で依頼する")
    void payUsesAmountDue() {
        Order order = standardOrder();
        order.confirmContent();
        order.fixAmountDue(new MemberDiscountPolicy());
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
            order.confirmContent();
            order.fixAmountDue(policy);
            RecordingPayment payment = new RecordingPayment();

            assertEquals(new Money(0), order.getAmountDue());
            assertTrue(order.pay(payment));
            assertEquals(OrderStatus.PAID, order.getStatus());
            assertEquals(0, payment.calls);
        }
    }

    @Test
    @DisplayName("支払額を固定する前は、内容を確定していても支払額を取得できない")
    void amountDueIsUnavailableBeforeFixing() {
        Order order = standardOrder();
        assertThrows(IllegalStateException.class, order::getAmountDue);

        order.confirmContent();
        IllegalStateException e = assertThrows(IllegalStateException.class, order::getAmountDue);
        assertEquals("支払額はまだ固定されていません", e.getMessage());
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
    @DisplayName("割引方針がnullなら支払額を固定せず、CONTENT_CONFIRMEDと明細を保ち、後で固定できる")
    void rejectsNullPolicy() {
        Order order = standardOrder();
        order.confirmContent();

        assertThrows(IllegalArgumentException.class, () -> order.fixAmountDue(null));
        assertEquals(OrderStatus.CONTENT_CONFIRMED, order.getStatus());
        assertEquals(2, order.getItems().size());
        assertThrows(IllegalStateException.class, order::getAmountDue);

        order.fixAmountDue(new MemberDiscountPolicy());
        assertEquals(new Money(1800), order.getAmountDue());
    }

    @Test
    @DisplayName("合計を超える割引額・nullの割引額を返す割引方針では、支払額を固定しない")
    void rejectsInvalidDiscountFromPolicy() {
        Order over = standardOrder();
        over.confirmContent();
        Order nullDiscount = standardOrder();
        nullDiscount.confirmContent();

        IllegalArgumentException e =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> over.fixAmountDue(new FixedDiscountPolicy(new Money(2001))));
        assertEquals("割引額が合計を超えています", e.getMessage());
        assertThrows(
                IllegalArgumentException.class,
                () -> nullDiscount.fixAmountDue(new FixedDiscountPolicy(null)));
        assertEquals(OrderStatus.CONTENT_CONFIRMED, over.getStatus());
        assertEquals(OrderStatus.CONTENT_CONFIRMED, nullDiscount.getStatus());
        assertThrows(IllegalStateException.class, over::getAmountDue);
        assertEquals(2, over.getItems().size());
    }

    @Test
    @DisplayName("割引額が合計と同じなら、支払額は0円で確定できる")
    void acceptsDiscountEqualToTotal() {
        Order order = standardOrder();
        order.confirmContent();
        order.fixAmountDue(new FixedDiscountPolicy(new Money(2000)));

        assertEquals(new Money(0), order.getAmountDue());
        assertTrue(order.pay(new RecordingPayment()));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("支払額を固定した注文は、割引を選び直せない。状態の違反を先に知らせる")
    void rejectsRefixingWithDiscount() {
        Order order = standardOrder();
        order.confirmContent();
        order.fixAmountDue(new MemberDiscountPolicy());

        IllegalStateException e =
                assertThrows(
                        IllegalStateException.class,
                        () -> order.fixAmountDue(new CouponDiscountPolicy()));
        assertEquals("支払額はすでに固定されています", e.getMessage());
        assertThrows(IllegalStateException.class, () -> order.fixAmountDue(null));
        assertEquals(OrderStatus.AMOUNT_FIXED, order.getStatus());
        assertEquals(new Money(1800), order.getAmountDue());
    }

    @Test
    @DisplayName("内容を確定する前の注文は、支払額を固定できない")
    void rejectsFixingBeforeContentConfirmed() {
        Order order = standardOrder();

        IllegalStateException e =
                assertThrows(
                        IllegalStateException.class,
                        () -> order.fixAmountDue(new MemberDiscountPolicy()));
        assertEquals("内容を確定する前は支払額を固定できません", e.getMessage());
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }
}
