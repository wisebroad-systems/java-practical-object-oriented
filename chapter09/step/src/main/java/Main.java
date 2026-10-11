public class Main {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository);

        Order order = createOrder();
        PaymentGateway failingGateway = new FakePaymentGateway(false);
        PaymentMethod firstCard = new CardPayment("card-0001", failingGateway);
        boolean first = service.confirmAndPay(order, new CouponDiscountPolicy(), firstCard);
        System.out.println("初回の結果：" + first);
        show("保存されている注文", repository.getById(new OrderId("order-001")));

        PaymentGateway workingGateway = new FakePaymentGateway(true);
        PaymentMethod retryCard = new CardPayment("card-0001", workingGateway);
        boolean retry = service.retryPayment(new OrderId("order-001"), retryCard);
        System.out.println("再試行の結果：" + retry);
        show("保存されている注文", repository.getById(new OrderId("order-001")));

        Order freeOrder = createFreeOrder();
        PaymentMethod freeCard = new CardPayment("card-0002", workingGateway);
        boolean free = service.confirmAndPay(freeOrder, new CouponDiscountPolicy(), freeCard);
        System.out.println("0円の注文の結果：" + free);
        show("保存されている注文", repository.getById(new OrderId("order-002")));
    }

    private static Order createOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        return order;
    }

    private static Order createFreeOrder() {
        // 200円のプリンにクーポン（300円まで）を使うと、支払額は0円になる
        Order order = new Order(new OrderId("order-002"), new TakeoutFulfillment(12));
        order.addItem(new MenuItem("プリン", new Money(200)), new Quantity(1));
        return order;
    }

    private static void show(String label, Order order) {
        String id = order.getId().getValue();
        int amountDue = order.getAmountDue().getYen();
        System.out.println(label + "：" + id + "、" + order.getStatus() + "、お支払い" + amountDue + "円");
    }
}
