public class DineInFulfillment implements FulfillmentMethod {
    private final int tableNumber;

    public DineInFulfillment(int tableNumber) {
        if (tableNumber < 1) {
            throw new IllegalArgumentException("テーブル番号は1以上にしてください");
        }
        this.tableNumber = tableNumber;
    }

    @Override
    public String instructions() {
        return "テーブル" + tableNumber + "へ配膳";
    }
}
