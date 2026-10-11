public class Main {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();

        Order order = createConfirmedOrder();
        repository.save(order);
        show("確定して保存", repository.getById(new OrderId("order-001")));

        PaymentGateway failingGateway = new FakePaymentGateway(false);
        boolean first = payAndSave(repository, new CardPayment("card-0001", failingGateway));
        System.out.println("支払いの結果：" + first);
        show("保存されている注文", repository.getById(new OrderId("order-001")));

        PaymentGateway workingGateway = new FakePaymentGateway(true);
        boolean retry = payAndSave(repository, new CardPayment("card-0001", workingGateway));
        System.out.println("再試行の結果：" + retry);
        show("支払い後に保存", repository.getById(new OrderId("order-001")));
    }

    private static boolean payAndSave(OrderRepository repository, PaymentMethod paymentMethod) {
        Order found = repository.getById(new OrderId("order-001"));
        boolean succeeded = found.pay(paymentMethod);
        if (succeeded) {
            repository.save(found);
        }
        return succeeded;
    }

    private static Order createConfirmedOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        order.confirm(new CouponDiscountPolicy());
        return order;
    }

    private static void show(String label, Order order) {
        String id = order.getId().getValue();
        int amountDue = order.getAmountDue().getYen();
        System.out.println(label + "：" + id + "、" + order.getStatus() + "、お支払い" + amountDue + "円");
    }
}
