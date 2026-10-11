public class RejectionExamples {
    public static void main(String[] args) {
        OrderRepository repository = new InMemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository);
        OrderId orderId = new OrderId("order-001");
        service.confirmOrder(createOrder());
        Order order = repository.getById(orderId);

        try {
            order.addItem(new MenuItem("プリン", new Money(200)), new Quantity(1));
        } catch (IllegalStateException e) {
            System.out.println("内容の確定後の追加：" + e.getMessage());
        }
        try {
            order.getAmountDue();
        } catch (IllegalStateException e) {
            System.out.println("固定前の支払額：" + e.getMessage());
        }
        PaymentMethod workingCard = new CardPayment("card-0001", new FakePaymentGateway(true));
        try {
            service.retryPayment(orderId, workingCard);
        } catch (IllegalStateException e) {
            System.out.println("固定前の再試行：" + e.getMessage());
        }

        PaymentMethod failingCard = new CardPayment("card-0001", new FakePaymentGateway(false));
        service.fixAmountAndPay(orderId, new CouponDiscountPolicy(), failingCard);
        try {
            service.fixAmountAndPay(orderId, new MemberDiscountPolicy(), failingCard);
        } catch (IllegalStateException e) {
            System.out.println("失敗後に別の割引：" + e.getMessage());
        }
        int amountDue = order.getAmountDue().getYen();
        System.out.println("注文の状態：" + order.getStatus() + "、お支払い" + amountDue + "円");
    }

    private static Order createOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        return order;
    }
}
