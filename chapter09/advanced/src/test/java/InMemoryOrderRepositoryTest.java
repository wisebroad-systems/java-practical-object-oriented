import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InMemoryOrderRepositoryTest {
    private static Order newOrder(String id) {
        return new Order(new OrderId(id), new DineInFulfillment(3));
    }

    @Test
    @DisplayName("保存した注文を、同じ文字列の別の注文IDで取得できる")
    void savesAndGetsById() {
        OrderRepository repository = new InMemoryOrderRepository();
        Order order = newOrder("order-001");
        repository.save(order);

        assertSame(order, repository.getById(new OrderId("order-001")));
    }

    @Test
    @DisplayName("注文ごとに別のIDで保存し、それぞれ取得できる")
    void keepsOrdersSeparately() {
        OrderRepository repository = new InMemoryOrderRepository();
        Order first = newOrder("order-001");
        Order second = newOrder("order-002");
        repository.save(first);
        repository.save(second);

        assertSame(first, repository.getById(new OrderId("order-001")));
        assertSame(second, repository.getById(new OrderId("order-002")));
    }

    @Test
    @DisplayName("同じIDで保存し直すと、新しく保存した注文に置き換わる")
    void saveWithSameIdUpdates() {
        OrderRepository repository = new InMemoryOrderRepository();
        repository.save(newOrder("order-001"));
        Order replaced = newOrder("order-001");
        repository.save(replaced);

        assertSame(replaced, repository.getById(new OrderId("order-001")));
    }

    @Test
    @DisplayName("メモリ上の保存は同じ注文への参照を持つので、再保存する前の変更も見える")
    void holdsSameReference() {
        OrderRepository repository = new InMemoryOrderRepository();
        Order order = newOrder("order-001");
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(1));
        repository.save(order);
        order.confirmContent();
        order.fixAmountDue(new NoDiscountPolicy());

        assertEquals(OrderStatus.AMOUNT_FIXED, repository.getById(order.getId()).getStatus());
    }

    @Test
    @DisplayName("保存されていない注文IDの取得はNoSuchElementExceptionで知らせる")
    void rejectsUnknownId() {
        OrderRepository repository = new InMemoryOrderRepository();
        repository.save(newOrder("order-001"));

        assertThrows(
                NoSuchElementException.class, () -> repository.getById(new OrderId("order-999")));
    }

    @Test
    @DisplayName("保存する注文・取得する注文IDのnullは拒否する")
    void rejectsNullArguments() {
        OrderRepository repository = new InMemoryOrderRepository();

        assertThrows(IllegalArgumentException.class, () -> repository.save(null));
        assertThrows(IllegalArgumentException.class, () -> repository.getById(null));
    }
}
