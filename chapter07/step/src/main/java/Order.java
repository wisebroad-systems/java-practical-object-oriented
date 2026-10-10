import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final List<OrderItem> items = new ArrayList<>();
    private final FulfillmentMethod fulfillmentMethod;
    private OrderStatus status = OrderStatus.DRAFT;
    private Money amountDue;

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
        confirm("none");
    }

    public void confirm(String discountType) {
        if (status != OrderStatus.DRAFT) {
            throw new IllegalStateException("注文はすでに確定しています");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("空の注文は確定できません");
        }
        if (discountType == null) {
            throw new IllegalArgumentException("割引の種類はnullにできません");
        }
        Money total = totalPrice();
        Money discount;
        switch (discountType) {
            case "none":
                discount = new Money(0);
                break;
            case "member":
                // 会員割引：明細合計の10％。1円未満は切り捨てる
                discount = new Money(total.getYen() / 10);
                break;
            case "coupon":
                // クーポン割引：300円。明細合計が300円より少なければ、明細合計と同じ額
                if (total.getYen() < 300) {
                    discount = total;
                } else {
                    discount = new Money(300);
                }
                break;
            default:
                throw new IllegalArgumentException("対応していない割引です：" + discountType);
        }
        amountDue = total.subtract(discount);
        status = OrderStatus.CONFIRMED;
    }

    public boolean pay(PaymentMethod paymentMethod) {
        if (status != OrderStatus.CONFIRMED) {
            throw new IllegalStateException("確定済み・未払いの注文だけ支払えます");
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
        if (status == OrderStatus.DRAFT) {
            throw new IllegalStateException("確定前の注文には支払額がありません");
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
