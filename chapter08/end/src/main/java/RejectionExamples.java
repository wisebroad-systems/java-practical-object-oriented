import java.util.NoSuchElementException;

public class RejectionExamples {
    public static void main(String[] args) {
        try {
            new OrderId(" ");
        } catch (IllegalArgumentException e) {
            System.out.println("空白の注文ID：" + e.getMessage());
        }
        try {
            new Order(null, new DineInFulfillment(3));
        } catch (IllegalArgumentException e) {
            System.out.println("注文IDなし：" + e.getMessage());
        }

        OrderRepository repository = new InMemoryOrderRepository();
        try {
            repository.getById(new OrderId("order-999"));
        } catch (NoSuchElementException e) {
            System.out.println("未登録の注文ID：" + e.getMessage());
        }

        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(1));
        order.confirm();
        order.pay(new CardPayment("card-0001", new FakePaymentGateway(true)));
        try {
            order.pay(new CardPayment("card-0001", new FakePaymentGateway(true)));
        } catch (IllegalStateException e) {
            System.out.println("支払い済みの注文の再支払い：" + e.getMessage());
        }
    }
}
