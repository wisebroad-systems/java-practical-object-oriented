import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    @DisplayName("作成直後の注文には料理がない")
    void newOrderHasNoItems() {
        Order order = new Order();

        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("開始時点では、取得したリストへ追加した料理が注文に残る")
    void keepsItemsAddedThroughList() {
        MenuItem coffee = new MenuItem("コーヒー", 400);
        MenuItem hamburger = new MenuItem("ハンバーグ", 1200);
        Order order = new Order();

        order.getItems().add(coffee);
        order.getItems().add(coffee);
        order.getItems().add(hamburger);

        assertEquals(3, order.getItems().size());
    }
}
