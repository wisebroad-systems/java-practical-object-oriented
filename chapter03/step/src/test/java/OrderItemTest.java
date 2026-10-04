import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderItemTest {
    private final MenuItem coffee = new MenuItem("コーヒー", 400);

    @Test
    @DisplayName("料理と数量を持つ明細を作れる")
    void createsOrderItem() {
        OrderItem item = new OrderItem(coffee, 2);

        assertEquals(coffee, item.getMenuItem());
        assertEquals(2, item.getQuantity());
    }

    @Test
    @DisplayName("数量が0や負の明細は作れない")
    void rejectsInvalidQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(coffee, 0));
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(coffee, -1));
    }

    @Test
    @DisplayName("料理がnullの明細は作れない")
    void rejectsNullMenuItem() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(null, 1));
    }
}
