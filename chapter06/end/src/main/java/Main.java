public class Main {
    public static void main(String[] args) {
        Order dineInOrder = new Order(new DineInFulfillment(3));
        prepare(dineInOrder);
        CashPayment cash = new CashPayment(new Money(3000));
        if (dineInOrder.pay(cash)) {
            System.out.println("お釣り：" + cash.getChange().getYen() + "円");
        }
        String dineIn = dineInOrder.fulfillmentInstructions();
        System.out.println("店内：" + dineIn + "、" + dineInOrder.getStatus());

        Order takeoutOrder = new Order(new TakeoutFulfillment(12));
        prepare(takeoutOrder);
        takeoutOrder.pay(new CardPayment("card-0001"));
        String takeout = takeoutOrder.fulfillmentInstructions();
        System.out.println("テイクアウト：" + takeout + "、" + takeoutOrder.getStatus());

        showMoneyAsValue();
        showPriceRevision();
    }

    private static void prepare(Order order) {
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        order.confirm();
    }

    private static void showMoneyAsValue() {
        Money price = new Money(400);
        Money samePrice = new Money(400);
        System.out.println("同じオブジェクトか：" + (price == samePrice));
        System.out.println("同じ金額か：" + price.equals(samePrice));

        Money doubled = price.multiply(new Quantity(2));
        System.out.println("2倍：" + doubled.getYen() + "円、元の金額：" + price.getYen() + "円");
    }

    private static void showPriceRevision() {
        Order order = new Order(new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(1));
        order.addItem(new MenuItem("コーヒー", new Money(450)), new Quantity(1));
        for (OrderItem item : order.getItems()) {
            MenuItem menuItem = item.getMenuItem();
            int price = menuItem.getPrice().getYen();
            System.out.println("明細：" + menuItem.getName() + "、" + price + "円");
        }
        System.out.println("合計：" + order.totalPrice().getYen() + "円");
    }
}
