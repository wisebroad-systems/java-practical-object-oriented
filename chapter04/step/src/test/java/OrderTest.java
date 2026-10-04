import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {
    private final MenuItem coffee = new MenuItem("コーヒー", 400);
    private final MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

    private Order confirmedOrder() {
        Order order = new Order();
        order.addItem(coffee, 2);
        order.addItem(hamburger, 1);
        order.confirm();
        return order;
    }

    @Test
    @DisplayName("作成直後の注文はDRAFT（未確定）")
    void newOrderIsDraft() {
        Order order = new Order();

        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertFalse(order.isConfirmed());
    }

    @Test
    @DisplayName("料理がある注文を確定するとCONFIRMED（確定済み・未払い）")
    void confirmMakesOrderConfirmed() {
        Order order = confirmedOrder();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
        assertTrue(order.isConfirmed());
        assertEquals(2000, order.totalPrice());
    }

    @Test
    @DisplayName("空の注文は確定できず、DRAFTのまま")
    void rejectsConfirmingEmptyOrder() {
        Order order = new Order();

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

        assertThrows(IllegalStateException.class, () -> order.addItem(coffee, 1));
        assertEquals(2, order.getItems().size());
        assertEquals(2000, order.totalPrice());
    }

    @Test
    @DisplayName("不正な数量では明細を追加せず、内容は変わらない")
    void rejectsInvalidQuantity() {
        Order order = new Order();
        order.addItem(coffee, 2);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(coffee, 0));
        assertEquals(1, order.getItems().size());
        assertEquals(800, order.totalPrice());
    }

    @Test
    @DisplayName("取得した一覧は変更できない")
    void itemsCannotBeModifiedThroughList() {
        Order order = confirmedOrder();
        List<OrderItem> items = order.getItems();

        assertThrows(UnsupportedOperationException.class, items::clear);
        assertEquals(2, order.getItems().size());
    }

    @Test
    @DisplayName("CONFIRMEDの注文は支払える。確認だけでは状態は変わらない")
    void checkPayableAllowsConfirmedOrder() {
        Order order = confirmedOrder();

        order.checkPayable();

        assertEquals(OrderStatus.CONFIRMED, order.getStatus());
    }

    @Test
    @DisplayName("未確定の注文は支払えない")
    void checkPayableRejectsDraftOrder() {
        Order order = new Order();
        order.addItem(coffee, 1);

        assertThrows(IllegalStateException.class, order::checkPayable);
        assertThrows(IllegalStateException.class, order::markPaid);
        assertEquals(OrderStatus.DRAFT, order.getStatus());
    }

    @Test
    @DisplayName("会計完了を記録するとPAID（支払い済み）になり、再度の支払いはできない")
    void markPaidMakesOrderPaid() {
        Order order = confirmedOrder();

        order.markPaid();

        assertEquals(OrderStatus.PAID, order.getStatus());
        assertTrue(order.isConfirmed());
        assertThrows(IllegalStateException.class, order::checkPayable);
        assertThrows(IllegalStateException.class, order::markPaid);
        assertThrows(IllegalStateException.class, order::confirm);
        assertThrows(IllegalStateException.class, () -> order.addItem(coffee, 1));
    }
}
