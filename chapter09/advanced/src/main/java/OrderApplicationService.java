public class OrderApplicationService {
    private final OrderRepository repository;

    public OrderApplicationService(OrderRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("注文の保存先はnullにできません");
        }
        this.repository = repository;
    }

    public void confirmOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("注文はnullにできません");
        }
        order.confirmContent();
        repository.save(order);
    }

    public boolean fixAmountAndPay(
            OrderId orderId, DiscountPolicy discountPolicy, PaymentMethod paymentMethod) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDはnullにできません");
        }
        if (discountPolicy == null) {
            throw new IllegalArgumentException("割引方針はnullにできません");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支払い方法はnullにできません");
        }
        Order order = repository.getById(orderId);
        order.fixAmountDue(discountPolicy);
        repository.save(order);
        return payAndSave(order, paymentMethod);
    }

    public boolean retryPayment(OrderId orderId, PaymentMethod paymentMethod) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDはnullにできません");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支払い方法はnullにできません");
        }
        Order order = repository.getById(orderId);
        return payAndSave(order, paymentMethod);
    }

    // 支払額の固定後と再試行で共通する部分。支払いの手順は注文に任せ、成功したときだけ保存し直す
    private boolean payAndSave(Order order, PaymentMethod paymentMethod) {
        boolean succeeded = order.pay(paymentMethod);
        if (succeeded) {
            repository.save(order);
        }
        return succeeded;
    }
}
