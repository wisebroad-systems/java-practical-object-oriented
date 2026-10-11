public class Main {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();

        Order order = createConfirmedOrder();
        repository.save(order);
        show("確定して保存", repository.getById(new OrderId("order-001")));

        Order found = repository.getById(new OrderId("order-001"));
        boolean succeeded = found.pay(new CardPayment("card-0001"));
        System.out.println("支払いの結果：" + succeeded);
        if (succeeded) {
            repository.save(found);
        }
        show("支払い後に保存", repository.getById(new OrderId("order-001")));
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
