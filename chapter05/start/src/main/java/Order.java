import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final List<OrderItem> items = new ArrayList<>();
    private OrderStatus status = OrderStatus.DRAFT;

    public void addItem(MenuItem menuItem, int quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("確定後は料理を追加できません");
        }
        OrderItem item = new OrderItem(menuItem, quantity);
        items.add(item);
    }

    public void confirm() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("注文はすでに確定しています");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("空の注文は確定できません");
        }
        status = OrderStatus.CONFIRMED;
    }

    public boolean pay(PaymentMethod paymentMethod) {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("確定済み・未払いの注文だけ支払えます");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支払い方法はnullにできません");
        }
        int amount = totalPrice();
        if (amount == 0) {
            status = OrderStatus.PAID;
            return true;
        }
        boolean succeeded = paymentMethod.pay(amount);
        if (succeeded) {
            status = OrderStatus.PAID;
        }
        return succeeded;
    }

    public boolean isConfirmed() {
        return status != OrderStatus.DRAFT;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public int totalPrice() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.subtotal();
        }
        return total;
    }
}
