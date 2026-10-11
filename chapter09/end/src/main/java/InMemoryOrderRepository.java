import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<OrderId, Order> orders = new HashMap<>();

    @Override
    public void save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("保存する注文はnullにできません");
        }
        orders.put(order.getId(), order);
    }

    @Override
    public Order getById(OrderId id) {
        if (id == null) {
            throw new IllegalArgumentException("注文IDはnullにできません");
        }
        Order order = orders.get(id);
        if (order == null) {
            throw new NoSuchElementException("注文が見つかりません：" + id.getValue());
        }
        return order;
    }
}
