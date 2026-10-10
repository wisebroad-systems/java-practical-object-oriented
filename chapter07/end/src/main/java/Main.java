public class Main {
    public static void main(String[] args) {
        Order noDiscountOrder = createOrder();
        noDiscountOrder.confirm();
        show("割引なし", noDiscountOrder);

        Order memberOrder = createOrder();
        memberOrder.confirm(new MemberDiscountPolicy());
        show("会員割引", memberOrder);

        Order couponOrder = createOrder();
        couponOrder.confirm(new CouponDiscountPolicy());
        show("クーポン", couponOrder);

        CashPayment cash = new CashPayment(new Money(2000));
        if (couponOrder.pay(cash)) {
            System.out.println("お釣り：" + cash.getChange().getYen() + "円");
        }
        System.out.println("クーポンの注文：" + couponOrder.getStatus());

        showSmallOrders();
    }

    private static Order createOrder() {
        Order order = new Order(new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        return order;
    }

    private static void showSmallOrders() {
        Order memberOrder = new Order(new DineInFulfillment(3));
        memberOrder.addItem(new MenuItem("日替わりランチ", new Money(999)), new Quantity(1));
        memberOrder.confirm(new MemberDiscountPolicy());
        show("会員割引", memberOrder);

        Order couponOrder = new Order(new DineInFulfillment(3));
        couponOrder.addItem(new MenuItem("プリン", new Money(200)), new Quantity(1));
        couponOrder.confirm(new CouponDiscountPolicy());
        show("クーポン", couponOrder);
    }

    private static void show(String label, Order order) {
        int total = order.totalPrice().getYen();
        int amountDue = order.getAmountDue().getYen();
        System.out.println(label + "：合計" + total + "円、お支払い" + amountDue + "円");
    }
}
