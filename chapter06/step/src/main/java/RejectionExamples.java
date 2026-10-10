public class RejectionExamples {
    public static void main(String[] args) {
        try {
            new Money(-1);
        } catch (IllegalArgumentException e) {
            System.out.println("負の金額：" + e.getMessage());
        }
        try {
            new Money(Integer.MAX_VALUE).add(new Money(1));
        } catch (ArithmeticException e) {
            System.out.println("intの範囲を超える金額：" + e.getMessage());
        }

        Order order = new Order(new DineInFulfillment(3));
        MenuItem coffee = new MenuItem("コーヒー", new Money(400));
        order.addItem(coffee, 1);
        try {
            order.addItem(coffee, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("数量0：" + e.getMessage());
        }
        System.out.println("明細の数：" + order.getItems().size());
    }
}
