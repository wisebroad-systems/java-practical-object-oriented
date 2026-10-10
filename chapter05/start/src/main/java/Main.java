public class Main {
    public static void main(String[] args) {
        Order cashOrder = createOrder();
        CashPayment cash = new CashPayment(3000);
        if (cashOrder.pay(cash)) {
            System.out.println("お釣り：" + cash.getChange() + "円");
        }
        System.out.println("現金：" + cashOrder.getStatus());

        Order cardOrder = createOrder();
        cardOrder.pay(new CardPayment("card-0001"));
        System.out.println("カード：" + cardOrder.getStatus());

        Order qrOrder = createOrder();
        qrOrder.pay(new QrPayment("qr-0001"));
        System.out.println("QRコード：" + qrOrder.getStatus());

        Order freeOrder = new Order();
        freeOrder.addItem(new MenuItem("お冷や", 0), 1);
        freeOrder.confirm();
        freeOrder.pay(new CardPayment("card-0001"));
        System.out.println("無料の注文：" + freeOrder.getStatus());
    }

    private static Order createOrder() {
        Order order = new Order();
        order.addItem(new MenuItem("コーヒー", 400), 2);
        order.addItem(new MenuItem("ハンバーグ", 1200), 1);
        order.confirm();
        return order;
    }
}
