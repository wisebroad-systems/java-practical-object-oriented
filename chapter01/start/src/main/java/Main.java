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
        int total = 0;
        for (MenuItem item : order.getItems()) {
            total += item.getPrice();
        }
        System.out.println("会計：" + total + "円");
    }

    private static void showOrderConfirmation(Order order) {
        int total = 0;
        for (MenuItem item : order.getItems()) {
            total += item.getPrice();
        }
        System.out.println("注文確認：" + total + "円");
    }
}
