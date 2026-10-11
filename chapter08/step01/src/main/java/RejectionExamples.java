public class RejectionExamples {
    public static void main(String[] args) {
        try {
            new OrderId(" ");
        } catch (IllegalArgumentException e) {
            System.out.println("空白の注文ID：" + e.getMessage());
        }
        try {
            new Order(null, new DineInFulfillment(3));
        } catch (IllegalArgumentException e) {
            System.out.println("注文IDなし：" + e.getMessage());
        }
    }
}
