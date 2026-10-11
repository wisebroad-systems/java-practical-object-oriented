public class Main {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository);
        OrderId orderId = new OrderId("order-001");

        service.confirmOrder(createOrder());
        Order confirmed = repository.getById(orderId);
        String id = confirmed.getId().getValue();
        System.out.println("内容を確定して保存：" + id + "、" + confirmed.getStatus());

        // 支払いの前に、お客さんがクーポンを提示した
        PaymentGateway failingGateway = new FakePaymentGateway(false);
        PaymentMethod firstCard = new CardPayment("card-0001", failingGateway);
        boolean first = service.fixAmountAndPay(orderId, new CouponDiscountPolicy(), firstCard);
        System.out.println("初回の結果：" + first);
        show("保存されている注文", repository.getById(orderId));

        PaymentGateway workingGateway = new FakePaymentGateway(true);
        PaymentMethod retryCard = new CardPayment("card-0001", workingGateway);
        boolean retry = service.retryPayment(orderId, retryCard);
        System.out.println("再試行の結果：" + retry);
        show("保存されている注文", repository.getById(orderId));
    }

    private static Order createOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        return order;
    }

    private static void show(String label, Order order) {
        String id = order.getId().getValue();
        int amountDue = order.getAmountDue().getYen();
        System.out.println(label + "：" + id + "、" + order.getStatus() + "、お支払い" + amountDue + "円");
    }
}
