import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final OrderId id;
    private final List<OrderItem> items = new ArrayList<>();
    private final FulfillmentMethod fulfillmentMethod;
    private OrderStatus status = OrderStatus.DRAFT;
    private Money amountDue;

    public Order(OrderId id, FulfillmentMethod fulfillmentMethod) {
        if (id == null) {
            throw new IllegalArgumentException("注文IDはnullにできません");
        }
        if (fulfillmentMethod == null) {
            throw new IllegalArgumentException("提供方法はnullにできません");
        }
        this.id = id;
        this.fulfillmentMethod = fulfillmentMethod;
    }

    public OrderId getId() {
        return id;
    }

    public void addItem(MenuItem menuItem, Quantity quantity) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("確定後は料理を追加できません");
        }
        OrderItem item = new OrderItem(menuItem, quantity);
        items.add(item);
    }

    public void confirmContent() {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("注文はすでに確定しています");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("空の注文は確定できません");
        }
        status = OrderStatus.CONTENT_CONFIRMED;
    }

    public void fixAmountDue(DiscountPolicy discountPolicy) {
        if (status == OrderStatus.DRAFT) {
            throw new IllegalStateException("内容を確定する前は支払額を固定できません");
        }
        if (status != OrderStatus.CONTENT_CONFIRMED) {
            throw new IllegalStateException("支払額はすでに固定されています");
        }
        if (discountPolicy == null) {
            throw new IllegalArgumentException("割引方針はnullにできません");
        }
        Money total = totalPrice();
        Money discount = discountPolicy.calculateDiscount(total);
        if (discount == null) {
            throw new IllegalArgumentException("割引額はnullにできません");
        }
        if (discount.getYen() > total.getYen()) {
            throw new IllegalArgumentException("割引額が合計を超えています");
        }
        amountDue = total.subtract(discount);
        status = OrderStatus.AMOUNT_FIXED;
    }

    public boolean pay(PaymentMethod paymentMethod) {
        if (status != OrderStatus.AMOUNT_FIXED) {
            throw new IllegalStateException("支払額を固定した未払いの注文だけ支払えます");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支払い方法はnullにできません");
        }
        Money amount = amountDue;
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

    public Money getAmountDue() {
        if (status == OrderStatus.DRAFT || status == OrderStatus.CONTENT_CONFIRMED) {
            throw new IllegalStateException("支払額はまだ固定されていません");
        }
        return amountDue;
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
