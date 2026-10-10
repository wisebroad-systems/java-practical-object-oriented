public class RejectionExamples {
    public static void main(String[] args) {
        try {
            new DineInFulfillment(0);
        } catch (IllegalArgumentException e) {
            System.out.println("テーブル番号0：" + e.getMessage());
        }
        try {
            new TakeoutFulfillment(0);
        } catch (IllegalArgumentException e) {
            System.out.println("受取番号0：" + e.getMessage());
        }
        try {
            new Order(null);
        } catch (IllegalArgumentException e) {
            System.out.println("提供方法なし：" + e.getMessage());
        }
    }
}
