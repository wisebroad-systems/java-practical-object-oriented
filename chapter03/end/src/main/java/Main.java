public class Main {
    public static void main(String[] args) {
        MenuItem coffee = new MenuItem("コーヒー", 400);
        MenuItem hamburger = new MenuItem("ハンバーグ", 1200);

        Order order = new Order();
        order.addItem(coffee, 2);
        order.addItem(hamburger, 1);
        order.confirm();

        for (OrderItem item : order.getItems()) {
            String name = item.getMenuItem().getName();
            System.out.println(name + " × " + item.getQuantity() + "：" + item.subtotal() + "円");
        }
        System.out.println("合計：" + order.totalPrice() + "円");
        System.out.println("確定済み：" + order.isConfirmed());
    }
}
