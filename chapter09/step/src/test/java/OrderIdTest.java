import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderIdTest {

    @Test
    @DisplayName("文字列で注文IDを作れる")
    void createsOrderId() {
        assertEquals("order-001", new OrderId("order-001").getValue());
    }

    @Test
    @DisplayName("null・空・空白だけの注文IDは作れない")
    void rejectsBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> new OrderId(null));
        assertThrows(IllegalArgumentException.class, () -> new OrderId(""));
        assertThrows(IllegalArgumentException.class, () -> new OrderId(" "));
    }

    @Test
    @DisplayName("同じ文字列なら、別のインスタンスでも等しく、hashCodeも同じ")
    void equalsByValue() {
        OrderId id = new OrderId("order-001");
        OrderId sameId = new OrderId("order-001");

        assertNotSame(id, sameId);
        assertEquals(id, sameId);
        assertEquals(id.hashCode(), sameId.hashCode());
        assertNotEquals(new OrderId("order-002"), id);
        assertNotEquals(null, id);
        assertNotEquals("order-001", id);
    }

    @Test
    @DisplayName("Mapのキーにすると、同じ文字列の別のインスタンスで取り出せる")
    void worksAsMapKey() {
        Map<OrderId, Order> orders = new HashMap<>();
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        orders.put(order.getId(), order);

        assertSame(order, orders.get(new OrderId("order-001")));
    }

    @Test
    @DisplayName("注文IDのクラスは継承できない")
    void isFinal() {
        assertTrue(Modifier.isFinal(OrderId.class.getModifiers()));
    }
}
