import java.util.NoSuchElementException;

public class RejectionExamples {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository);
        PaymentMethod card = new CardPayment("card-0001", new FakePaymentGateway(true));

        Order order = createOrder("order-001");
        try {
            service.confirmAndPay(order, new NoDiscountPolicy(), null);
        } catch (IllegalArgumentException e) {
            System.out.println("支払い方法なし：" + e.getMessage());
        }
        System.out.println("注文の状態：" + order.getStatus());
        try {
            repository.getById(new OrderId("order-001"));
        } catch (NoSuchElementException e) {
            System.out.println("保存先：" + e.getMessage());
        }

        try {
            service.retryPayment(new OrderId("order-999"), card);
        } catch (NoSuchElementException e) {
            System.out.println("未登録の注文の再試行：" + e.getMessage());
        }

        service.confirmAndPay(createOrder("order-003"), new NoDiscountPolicy(), card);
        try {
            service.retryPayment(new OrderId("order-003"), card);
        } catch (IllegalStateException e) {
            System.out.println("支払い済みの注文の再試行：" + e.getMessage());
        }
    }

    private static Order createOrder(String id) {
        Order order = new Order(new OrderId(id), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(1));
        return order;
    }
}
