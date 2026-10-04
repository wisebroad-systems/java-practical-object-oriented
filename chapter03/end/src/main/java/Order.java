import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final List<OrderItem> items = new ArrayList<>();
    private boolean confirmed = false;

    public void addItem(MenuItem menuItem, int quantity) {
        if (confirmed) {
            throw new IllegalStateException("確定後は料理を追加できません");
        }
        OrderItem item = new OrderItem(menuItem, quantity);
        items.add(item);
    }

    public void confirm() {
        if (confirmed) {
            throw new IllegalStateException("注文はすでに確定しています");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("空の注文は確定できません");
        }
        confirmed = true;
    }

    public boolean isConfirmed() {
        return confirmed;
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
