public class OrderApplicationService {
    private final OrderRepository repository;

    public OrderApplicationService(OrderRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("注文の保存先はnullにできません");
        }
        this.repository = repository;
    }

    public boolean confirmAndPay(
            Order order, DiscountPolicy discountPolicy, PaymentMethod paymentMethod) {
        if (order == null) {
            throw new IllegalArgumentException("注文はnullにできません");
        }
        if (discountPolicy == null) {
            throw new IllegalArgumentException("割引方針はnullにできません");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支払い方法はnullにできません");
        }
        order.confirm(discountPolicy);
        repository.save(order);
        boolean succeeded = order.pay(paymentMethod);
        if (succeeded) {
            repository.save(order);
        }
        return succeeded;
    }

    public boolean retryPayment(OrderId orderId, PaymentMethod paymentMethod) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDはnullにできません");
        }
        if (paymentMethod == null) {
            throw new IllegalArgumentException("支払い方法はnullにできません");
        }
        Order order = repository.getById(orderId);
        boolean succeeded = order.pay(paymentMethod);
        if (succeeded) {
            repository.save(order);
        }
        return succeeded;
    }
}
