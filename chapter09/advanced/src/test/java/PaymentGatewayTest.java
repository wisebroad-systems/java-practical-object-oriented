import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentGatewayTest {

    /** 受け取った依頼を記録し、決めておいた結果を返す決済の窓口（テスト用）。 */
    private static class RecordingGateway implements PaymentGateway {
        private final boolean result;
        private int calls = 0;
        private String method;
        private String identifier;
        private Money amount;

        RecordingGateway(boolean result) {
            this.result = result;
        }

        @Override
        public boolean payByCard(String cardToken, Money amount) {
            record("card", cardToken, amount);
            return result;
        }

        @Override
        public boolean payByQr(String paymentCode, Money amount) {
            record("qr", paymentCode, amount);
            return result;
        }

        private void record(String method, String identifier, Money amount) {
            calls++;
            this.method = method;
            this.identifier = identifier;
            this.amount = amount;
        }
    }

    private static Order confirmedOrder(String id) {
        Order order = new Order(new OrderId(id), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        order.confirmContent();
        order.fixAmountDue(new CouponDiscountPolicy());
        return order;
    }

    @Test
    @DisplayName("カードは、カードの識別情報と支払う金額を決済の窓口へ渡し、その結果を返す")
    void cardDelegatesToGateway() {
        RecordingGateway gateway = new RecordingGateway(true);

        assertTrue(new CardPayment("card-0001", gateway).pay(new Money(1700)));
        assertEquals("card", gateway.method);
        assertEquals("card-0001", gateway.identifier);
        assertEquals(new Money(1700), gateway.amount);
        assertFalse(new CardPayment("card-0001", new RecordingGateway(false)).pay(new Money(1)));
    }

    @Test
    @DisplayName("QRコードは、決済コードと支払う金額を決済の窓口へ渡し、その結果を返す")
    void qrDelegatesToGateway() {
        RecordingGateway gateway = new RecordingGateway(true);

        assertTrue(new QrPayment("qr-0001", gateway).pay(new Money(1700)));
        assertEquals("qr", gateway.method);
        assertEquals("qr-0001", gateway.identifier);
        assertEquals(new Money(1700), gateway.amount);
        assertFalse(new QrPayment("qr-0001", new RecordingGateway(false)).pay(new Money(1)));
    }

    @Test
    @DisplayName("カード・QRコードは、決済の窓口がnullなら作れない")
    void rejectsNullGateway() {
        assertThrows(IllegalArgumentException.class, () -> new CardPayment("card-0001", null));
        assertThrows(IllegalArgumentException.class, () -> new QrPayment("qr-0001", null));
    }

    @Test
    @DisplayName("代替の決済の窓口は、作成時に指定した結果を返す。引数のnullは拒否する")
    void fakeGatewayReturnsConfiguredResult() {
        PaymentGateway working = new FakePaymentGateway(true);
        PaymentGateway failing = new FakePaymentGateway(false);

        assertTrue(working.payByCard("card-0001", new Money(1700)));
        assertTrue(working.payByQr("qr-0001", new Money(1700)));
        assertFalse(failing.payByCard("card-0001", new Money(1700)));
        assertFalse(failing.payByQr("qr-0001", new Money(1700)));
        assertThrows(IllegalArgumentException.class, () -> working.payByCard(null, new Money(1)));
        assertThrows(IllegalArgumentException.class, () -> working.payByCard("card-0001", null));
        assertThrows(IllegalArgumentException.class, () -> working.payByQr(null, new Money(1)));
        assertThrows(IllegalArgumentException.class, () -> working.payByQr("qr-0001", null));
    }

    @Test
    @DisplayName("決済に失敗すると、注文はAMOUNT_FIXEDのまま。支払額は変わらない")
    void failedPaymentKeepsOrderUnpaid() {
        Order order = confirmedOrder("order-001");

        assertFalse(order.pay(new CardPayment("card-0001", new RecordingGateway(false))));
        assertEquals(OrderStatus.AMOUNT_FIXED, order.getStatus());
        assertEquals(new Money(1700), order.getAmountDue());
    }

    @Test
    @DisplayName("失敗した注文を保存先から取得し、成功する決済の窓口で、固定した金額のまま再試行できる")
    void retryUsesFixedAmount() {
        OrderRepository repository = new InMemoryOrderRepository();
        Order order = confirmedOrder("order-001");
        repository.save(order);
        order.pay(new CardPayment("card-0001", new RecordingGateway(false)));

        Order found = repository.getById(new OrderId("order-001"));
        RecordingGateway gateway = new RecordingGateway(true);
        assertTrue(found.pay(new CardPayment("card-0001", gateway)));
        repository.save(found);

        assertEquals(new Money(1700), gateway.amount);
        assertEquals(OrderStatus.PAID, repository.getById(new OrderId("order-001")).getStatus());
    }

    @Test
    @DisplayName("支払額が0円なら、決済の窓口を呼ばずにPAIDになる")
    void zeroAmountDoesNotCallGateway() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("プリン", new Money(200)), new Quantity(1));
        order.confirmContent();
        order.fixAmountDue(new CouponDiscountPolicy());
        RecordingGateway gateway = new RecordingGateway(true);

        assertTrue(order.pay(new CardPayment("card-0001", gateway)));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, gateway.calls);
    }

    @Test
    @DisplayName("支払い済みの注文を再試行すると拒否し、決済の窓口を呼ばない")
    void paidOrderRetryDoesNotCallGateway() {
        Order order = confirmedOrder("order-001");
        order.pay(new CardPayment("card-0001", new RecordingGateway(true)));
        RecordingGateway gateway = new RecordingGateway(true);

        assertThrows(
                IllegalStateException.class,
                () -> order.pay(new CardPayment("card-0001", gateway)));
        assertEquals(0, gateway.calls);
    }
}
