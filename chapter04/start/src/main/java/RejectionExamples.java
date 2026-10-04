public class RejectionExamples {
    public static void main(String[] args) {
        MenuItem coffee = new MenuItem("コーヒー", 400);
        Order order = new Order();
        order.addItem(coffee, 2);

        try {
            order.addItem(coffee, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("数量0での追加：" + e.getMessage());
        }
        printOrder(order);

        try {
            order.addItem(coffee, -1);
        } catch (IllegalArgumentException e) {
            System.out.println("負の数量での追加：" + e.getMessage());
        }
        printOrder(order);

        order.confirm();
        try {
            order.addItem(coffee, 1);
        } catch (IllegalStateException e) {
            System.out.println("確定後の追加：" + e.getMessage());
        }
        printOrder(order);
    }

    private static void printOrder(Order order) {
        int itemCount = order.getItems().size();
        int quantity = order.getItems().get(0).getQuantity();
        int total = order.totalPrice();
        boolean confirmed = order.isConfirmed();
        String detail = "明細数：" + itemCount + "、数量：" + quantity;
        System.out.println(detail + "、合計：" + total + "円、確定済み：" + confirmed);
    }
}
