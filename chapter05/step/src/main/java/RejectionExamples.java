public class RejectionExamples {
    public static void main(String[] args) {
        try {
            new DineInOrder(0);
        } catch (IllegalArgumentException e) {
            System.out.println("テーブル番号0：" + e.getMessage());
        }
        try {
            new TakeoutOrder(0);
        } catch (IllegalArgumentException e) {
            System.out.println("受取番号0：" + e.getMessage());
        }
    }
}
