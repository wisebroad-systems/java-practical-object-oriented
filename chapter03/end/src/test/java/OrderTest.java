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

    @Test
    @DisplayName("料理と数量で明細を追加し、合計を求める")
    void calculatesTotalPriceWithQuantities() {
        Order order = new Order();
        order.addItem(coffee, 2);
        order.addItem(hamburger, 1);

        assertEquals(2, order.getItems().size());
        assertEquals(2000, order.totalPrice());
    }

    @Test
    @DisplayName("不正な数量では明細を追加せず、既存の明細・合計・状態は変わらない")
    void rejectsInvalidQuantity() {
        Order order = new Order();
        order.addItem(coffee, 2);

        assertThrows(IllegalArgumentException.class, () -> order.addItem(coffee, 0));
        assertThrows(IllegalArgumentException.class, () -> order.addItem(coffee, -1));
        assertEquals(1, order.getItems().size());
        assertEquals(2, order.getItems().get(0).getQuantity());
        assertEquals(800, order.totalPrice());
        assertFalse(order.isConfirmed());
    }

    @Test
    @DisplayName("nullの料理では明細を追加しない")
    void rejectsNullMenuItem() {
        Order order = new Order();

        assertThrows(IllegalArgumentException.class, () -> order.addItem(null, 1));
        assertEquals(0, order.getItems().size());
    }

    @Test
    @DisplayName("確定後の追加は、数量が不正でも状態の違反として拒否する")
    void checksStateBeforeArgument() {
        Order order = new Order();
        order.addItem(coffee, 1);
        order.confirm();

        assertThrows(IllegalStateException.class, () -> order.addItem(coffee, 0));
    }

    @Test
    @DisplayName("同じ料理を後から追加すると、別の明細になる")
    void doesNotMergeSameMenuItem() {
        Order order = new Order();
        order.addItem(coffee, 2);
        order.addItem(coffee, 1);

        assertEquals(2, order.getItems().size());
        assertEquals(1200, order.totalPrice());
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
        order.addItem(coffee, 1);

        order.confirm();

        assertTrue(order.isConfirmed());
    }

    @Test
    @DisplayName("0円の料理だけの注文も確定できる")
    void confirmsOrderWithOnlyFreeItems() {
        Order order = new Order();
        order.addItem(new MenuItem("お冷や", 0), 1);

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
        order.addItem(coffee, 1);
        order.confirm();

        assertThrows(IllegalStateException.class, order::confirm);
        assertTrue(order.isConfirmed());
    }

    @Test
    @DisplayName("確定後は料理を追加できず、内容と合計は変わらない")
    void rejectsAddingAfterConfirm() {
        Order order = new Order();
        order.addItem(coffee, 1);
        order.confirm();

        assertThrows(
                IllegalStateException.class,
                () -> {
                    order.addItem(hamburger, 1);
                });
        assertEquals(1, order.getItems().size());
        assertEquals(400, order.totalPrice());
        assertTrue(order.isConfirmed());
    }

    @Test
    @DisplayName("取得した一覧は変更できず、注文の内容は変わらない")
    void itemsCannotBeModifiedThroughList() {
        Order order = new Order();
        order.addItem(coffee, 1);
        order.confirm();

        assertThrows(UnsupportedOperationException.class, () -> order.getItems().clear());
        assertEquals(1, order.getItems().size());
        assertEquals(400, order.totalPrice());
    }

    @Test
    @DisplayName("取得済みの一覧は後の追加に追従せず、取り直すと反映される")
    void listIsSnapshot() {
        Order order = new Order();
        order.addItem(coffee, 1);
        List<?> items = order.getItems();

        order.addItem(hamburger, 1);

        assertEquals(1, items.size());
        assertEquals(2, order.getItems().size());
    }
}
