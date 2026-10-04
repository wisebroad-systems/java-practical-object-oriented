public class RejectionExamples {
    public static void main(String[] args) {
        MenuItem coffee = new MenuItem("コーヒー", 400);

        Order emptyOrder = new Order();
        try {
            emptyOrder.confirm();
        } catch (IllegalStateException e) {
            System.out.println("空の注文：" + e.getMessage());
        }
        printOrder(emptyOrder);

        Order order = new Order();
        order.addItem(coffee);
        order.confirm();

        try {
            order.addItem(coffee);
        } catch (IllegalStateException e) {
            System.out.println("確定後の追加：" + e.getMessage());
        }
        printOrder(order);

        try {
            order.confirm();
        } catch (IllegalStateException e) {
            System.out.println("再確定：" + e.getMessage());
        }
        printOrder(order);

        try {
            order.getItems().clear();
        } catch (UnsupportedOperationException e) {
            System.out.println("リスト経由の変更：変更できません");
        }
        printOrder(order);
    }

    private static void printOrder(Order order) {
        int itemCount = order.getItems().size();
        int total = order.totalPrice();
        boolean confirmed = order.isConfirmed();
        System.out.println("料理数：" + itemCount + "、合計：" + total + "円、確定済み：" + confirmed);
    }
}
