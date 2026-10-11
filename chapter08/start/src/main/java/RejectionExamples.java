public class RejectionExamples {
    public static void main(String[] args) {
        try {
            new Money(100).subtract(new Money(300));
        } catch (IllegalArgumentException e) {
            System.out.println("負になる引き算：" + e.getMessage());
        }

        Order order = new Order(new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        try {
            order.getAmountDue();
        } catch (IllegalStateException e) {
            System.out.println("未確定の支払額：" + e.getMessage());
        }
        try {
            order.confirm(null);
        } catch (IllegalArgumentException e) {
            System.out.println("割引方針なし：" + e.getMessage());
        }
        System.out.println("状態：" + order.getStatus());
    }
}
