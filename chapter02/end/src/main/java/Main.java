public class Main {
    public static void main(String[] args) {
        MenuItem coffee = new MenuItem("コーヒー", 400);
        MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

        Order order = new Order();
        order.addItem(coffee);
        order.addItem(coffee);
        order.addItem(hamburger);

        order.confirm();

        System.out.println("注文確認：" + order.totalPrice() + "円");
        System.out.println("確定済み：" + order.isConfirmed());
        for (MenuItem item : order.getItems()) {
            System.out.println(item.getName());
        }
    }
}
