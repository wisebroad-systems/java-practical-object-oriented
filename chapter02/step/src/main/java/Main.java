public class Main {
    public static void main(String[] args) {
        MenuItem coffee = new MenuItem("コーヒー", 400);
        MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

        Order order = new Order();
        order.addItem(coffee);
        order.addItem(coffee);
        order.addItem(hamburger);
        order.confirm();
        System.out.println("確定済み：" + order.isConfirmed() + "、合計：" + order.totalPrice() + "円");

        order.getItems().clear();
        System.out.println("確定済み：" + order.isConfirmed() + "、合計：" + order.totalPrice() + "円");
    }
}
