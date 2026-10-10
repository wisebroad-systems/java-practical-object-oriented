public class DineInOrder extends Order {
    private final int tableNumber;

    public DineInOrder(int tableNumber) {
        if (tableNumber < 1) {
            throw new IllegalArgumentException("テーブル番号は1以上にしてください");
        }
        this.tableNumber = tableNumber;
    }

    @Override
    public String fulfillmentInstructions() {
        return "テーブル" + tableNumber + "へ配膳";
    }
}
