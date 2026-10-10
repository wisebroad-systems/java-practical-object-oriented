public class Main {
    public static void main(String[] args) {
        Order dineInOrder = new Order(new DineInFulfillment(3));
        prepare(dineInOrder);
        CashPayment cash = new CashPayment(3000);
        if (dineInOrder.pay(cash)) {
            System.out.println("お釣り：" + cash.getChange() + "円");
        }
        String dineIn = dineInOrder.fulfillmentInstructions();
        System.out.println("店内：" + dineIn + "、" + dineInOrder.getStatus());

        Order takeoutOrder = new Order(new TakeoutFulfillment(12));
        prepare(takeoutOrder);
        takeoutOrder.pay(new CardPayment("card-0001"));
        String takeout = takeoutOrder.fulfillmentInstructions();
        System.out.println("テイクアウト：" + takeout + "、" + takeoutOrder.getStatus());
    }

    private static void prepare(Order order) {
        order.addItem(new MenuItem("コーヒー", 400), 2);
        order.addItem(new MenuItem("ハンバーグ", 1200), 1);
        order.confirm();
    }
}
