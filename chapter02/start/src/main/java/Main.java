public class Main {
    public static void main(String[] args) {
        MenuItem coffee = new MenuItem("コーヒー", 400);
        MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

        Order order = new Order();
        order.getItems().add(coffee);
        order.getItems().add(coffee);
        order.getItems().add(hamburger);

        showBill(order);
        showOrderConfirmation(order);
    }

    private static void showBill(Order order) {
        System.out.println("会計：" + order.totalPrice() + "円");
    }

    private static void showOrderConfirmation(Order order) {
        System.out.println("注文確認：" + order.totalPrice() + "円");
    }
}
