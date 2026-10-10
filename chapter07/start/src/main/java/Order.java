import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final List<OrderItem> items = new ArrayList<>();
    private final FulfillmentMethod fulfillmentMethod;
    private OrderStatus status = OrderStatus.DRAFT;

    public Order(FulfillmentMethod fulfillmentMethod) {
        if (fulfillmentMethod == null) {
            throw new IllegalArgumentException("提供方法はnullにできません");
        }
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public void addItem(MenuItem menuItem, Quantity quantity) {
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
        Money amount = totalPrice();
        if (amount.equals(new Money(0))) {
            status = OrderStatus.PAID;
            return true;
        }
        boolean succeeded = paymentMethod.pay(amount);
        if (succeeded) {
            status = OrderStatus.PAID;
        }
        return succeeded;
    }

    public String fulfillmentInstructions() {
        return fulfillmentMethod.instructions();
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

    public Money totalPrice() {
        Money total = new Money(0);
        for (OrderItem item : items) {
            total = total.add(item.subtotal());
        }
        return total;
    }
}
