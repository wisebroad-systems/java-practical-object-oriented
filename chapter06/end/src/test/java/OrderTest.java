import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {
    private static Order newOrder() {
        return new Order(new DineInFulfillment(3));
    }

    private final MenuItem coffee = new MenuItem("コーヒー", new Money(400));
    private final MenuItem hamburger = new MenuItem("ハンバーグ", new Money(1200));

    private Order confirmedOrder() {
        Order order = newOrder();
        order.addItem(coffee, new Quantity(2));
        order.addItem(hamburger, new Quantity(1));
        order.confirm();
        return order;
    }

    @Test
    @DisplayName("作成直後の注文はDRAFT（未確定）")
    void newOrderIsDraft() {
        Order order = newOrder();

        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertFalse(order.isConfirmed());
    }

    @Test
    @DisplayName("料理がある注文を確定するとCONFIRMED（確定済み・未払い）")
    void confirmMakesOrderConfirmed() {
        Order order = confirmedOrder();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertTrue(order.isConfirmed());
        assertEquals(new Money(2000), order.totalPrice());
    }

    @Test
    @DisplayName("空の注文は確定できず、DRAFTのまま")
    void rejectsConfirmingEmptyOrder() {
        Order order = newOrder();

        assertThrows(IllegalStateException.class, order::confirm);
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    @DisplayName("確定済みの注文は再確定できない")
    void rejectsConfirmingTwice() {
        Order order = confirmedOrder();

        assertThrows(IllegalStateException.class, order::confirm);
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("確定後は料理を追加できず、内容と合計は変わらない")
    void rejectsAddingAfterConfirm() {
        Order order = confirmedOrder();

        assertThrows(IllegalStateException.class, () -> order.addItem(coffee, new Quantity(1)));
        assertEquals(2, order.getItems().size());
        assertEquals(new Money(2000), order.totalPrice());
    }

    @Test
    @DisplayName("不正な数量では明細を追加せず、内容は変わらない")
    void rejectsInvalidQuantity() {
        Order order = newOrder();
        order.addItem(coffee, new Quantity(2));

        assertThrows(IllegalArgumentException.class, () -> order.addItem(coffee, new Quantity(0)));
        assertEquals(1, order.getItems().size());
        assertEquals(new Money(800), order.totalPrice());
    }

    @Test
    @DisplayName("取得した一覧は変更できない")
    void itemsCannotBeModifiedThroughList() {
        Order order = confirmedOrder();
        List<OrderItem> items = order.getItems();

        assertThrows(UnsupportedOperationException.class, items::clear);
        assertEquals(2, order.getItems().size());
    }

    /** 渡された金額を記録し、決めておいた結果を返す支払い方法（テスト用）。 */
    private static class RecordingPayment implements PaymentMethod {
        private final boolean result;
        private int calls = 0;
        private Money paidAmount;

        RecordingPayment(boolean result) {
            this.result = result;
        }

        @Override
        public boolean pay(Money amount) {
            calls++;
            paidAmount = amount;
            return result;
        }
    }

    @Test
    @DisplayName("支払いに成功するとPAIDになり、合計の金額で支払いを依頼する")
    void paySucceeds() {
        Order order = confirmedOrder();
        RecordingPayment payment = new RecordingPayment(true);

        assertTrue(order.pay(payment));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(1, payment.calls);
        assertEquals(new Money(2000), payment.paidAmount);
    }

    @Test
    @DisplayName("支払いに失敗するとfalseを返し、CONFIRMEDのまま")
    void payFails() {
        Order order = confirmedOrder();

        assertFalse(order.pay(new RecordingPayment(false)));
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("0円の注文は支払いを依頼せずにPAIDになる")
    void zeroAmountCompletesWithoutPayment() {
        Order order = newOrder();
        order.addItem(new MenuItem("お冷や", new Money(0)), new Quantity(1));
        order.confirm();
        RecordingPayment payment = new RecordingPayment(true);

        assertTrue(order.pay(payment));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, payment.calls);
    }

    @Test
    @DisplayName("未確定の注文は支払えず、支払いを依頼しない")
    void rejectsPayingDraftOrder() {
        Order order = newOrder();
        order.addItem(coffee, new Quantity(1));
        RecordingPayment payment = new RecordingPayment(true);

        assertThrows(IllegalStateException.class, () -> order.pay(payment));
        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertEquals(0, payment.calls);
    }

    @Test
    @DisplayName("支払い済みの注文は再度支払えず、支払いを依頼しない")
    void rejectsPayingTwice() {
        Order order = confirmedOrder();
        order.pay(new RecordingPayment(true));
        RecordingPayment second = new RecordingPayment(true);

        assertThrows(IllegalStateException.class, () -> order.pay(second));
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(0, second.calls);
    }

    @Test
    @DisplayName("支払い方法がnullなら拒否する。未確定なら状態の違反を先に知らせる")
    void rejectsNullPaymentMethod() {
        Order confirmed = confirmedOrder();
        assertThrows(IllegalArgumentException.class, () -> confirmed.pay(null));
        assertEquals(OrderStatus.CONFIRMED, confirmed.getStatus());

        Order draft = newOrder();
        draft.addItem(coffee, new Quantity(1));
        assertThrows(IllegalStateException.class, () -> draft.pay(null));
    }

    @Test
    @DisplayName("現金が足りなければfalseを返し、CONFIRMEDのまま")
    void insufficientCashKeepsOrderUnpaid() {
        Order order = confirmedOrder();

        assertFalse(order.pay(new CashPayment(new Money(1000))));
        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("料理のない注文の合計は0円")
    void emptyOrderTotalIsZero() {
        assertEquals(new Money(0), newOrder().totalPrice());
    }

    @Test
    @DisplayName("価格改定後のメニュー項目を追加しても、前に追加した明細の価格は変わらない")
    void priceRevisionAppliesOnlyToNewItems() {
        Order order = newOrder();
        order.addItem(coffee, new Quantity(1));
        MenuItem revisedCoffee = new MenuItem("コーヒー", new Money(450));
        order.addItem(revisedCoffee, new Quantity(1));

        List<OrderItem> items = order.getItems();
        assertEquals(new Money(400), items.get(0).subtotal());
        assertEquals(new Money(450), items.get(1).subtotal());
        assertEquals(new Money(850), order.totalPrice());
    }
}
