import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderItemTest {
    private final MenuItem coffee = new MenuItem("コーヒー", new Money(400));

    @Test
    @DisplayName("料理と数量を持つ明細を作れる")
    void createsOrderItem() {
        OrderItem item = new OrderItem(coffee, new Quantity(2));

        assertEquals(coffee, item.getMenuItem());
        assertEquals(new Quantity(2), item.getQuantity());
    }

    @Test
    @DisplayName("数量がnullの明細は作れない（0や負の数量はQuantityが拒否する）")
    void rejectsNullQuantity() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(coffee, null));
    }

    @Test
    @DisplayName("料理がnullの明細は作れない")
    void rejectsNullMenuItem() {
        assertThrows(IllegalArgumentException.class, () -> new OrderItem(null, new Quantity(1)));
    }

    @Test
    @DisplayName("明細が料理の価格と数量から小計を求める")
    void calculatesSubtotal() {
        assertEquals(new Money(800), new OrderItem(coffee, new Quantity(2)).subtotal());
    }
}
