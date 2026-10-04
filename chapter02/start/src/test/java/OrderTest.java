import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderTest {
    private final MenuItem coffee = new MenuItem("コーヒー", 400);
    private final MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

    @Test
    @DisplayName("注文が料理の価格を足して合計を求める")
    void calculatesTotalPrice() {
        Order order = new Order();
        order.getItems().add(coffee);
        order.getItems().add(coffee);
        order.getItems().add(hamburger);

        assertEquals(2000, order.totalPrice());
    }

    @Test
    @DisplayName("料理がない注文の合計は0円")
    void totalPriceOfEmptyOrderIsZero() {
        assertEquals(0, new Order().totalPrice());
    }
}
