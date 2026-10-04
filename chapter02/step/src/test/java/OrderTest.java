import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {
    private final MenuItem coffee = new MenuItem("コーヒー", 400);
    private final MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

    @Test
    @DisplayName("addItemで追加した料理の合計を求める")
    void calculatesTotalPriceOfAddedItems() {
        Order order = new Order();
        order.addItem(coffee);
        order.addItem(coffee);
        order.addItem(hamburger);

        assertEquals(3, order.getItems().size());
        assertEquals(2000, order.totalPrice());
    }

    @Test
    @DisplayName("nullの料理は追加できず、内容は変わらない")
    void rejectsNullItem() {
        Order order = new Order();
        order.addItem(coffee);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(null));
        assertEquals(1, order.getItems().size());
        assertFalse(order.isConfirmed());
    }

    @Test
    @DisplayName("確定後の追加は、料理がnullでも状態の違反として拒否する")
    void checksStateBeforeArgument() {
        Order order = new Order();
        order.addItem(coffee);
        order.confirm();

        assertThrows(IllegalStateException.class, () -> order.addItem(null));
    }

    @Test
    @DisplayName("作成直後の注文は未確定")
    void newOrderIsNotConfirmed() {
        assertFalse(new Order().isConfirmed());
    }

    @Test
    @DisplayName("料理がある注文は確定できる")
    void confirmsOrderWithItems() {
        Order order = new Order();
        order.addItem(coffee);

        order.confirm();

        assertTrue(order.isConfirmed());
    }

    @Test
    @DisplayName("0円の料理だけの注文も確定できる")
    void confirmsOrderWithOnlyFreeItems() {
        Order order = new Order();
        order.addItem(new MenuItem("お冷や", 0));

        order.confirm();

        assertTrue(order.isConfirmed());
        assertEquals(0, order.totalPrice());
    }

    @Test
    @DisplayName("空の注文は確定できず、未確定のまま")
    void rejectsConfirmingEmptyOrder() {
        Order order = new Order();

        assertThrows(IllegalStateException.class, order::confirm);
        assertFalse(order.isConfirmed());
        assertEquals(0, order.getItems().size());
    }

    @Test
    @DisplayName("確定済みの注文は再確定できず、確定済みのまま")
    void rejectsConfirmingTwice() {
        Order order = new Order();
        order.addItem(coffee);
        order.confirm();

        assertThrows(IllegalStateException.class, order::confirm);
        assertTrue(order.isConfirmed());
    }

    @Test
    @DisplayName("確定後は料理を追加できず、内容と合計は変わらない")
    void rejectsAddingAfterConfirm() {
        Order order = new Order();
        order.addItem(coffee);
        order.confirm();

        assertThrows(
                IllegalStateException.class,
                () -> {
                    order.addItem(hamburger);
                });
        assertEquals(1, order.getItems().size());
        assertEquals(400, order.totalPrice());
        assertTrue(order.isConfirmed());
    }
}
