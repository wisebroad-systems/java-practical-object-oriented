public class TakeoutOrder extends Order {
    private final int pickupNumber;

    public TakeoutOrder(int pickupNumber) {
        if (pickupNumber < 1) {
            throw new IllegalArgumentException("受取番号は1以上にしてください");
        }
        this.pickupNumber = pickupNumber;
    }

    @Override
    public String fulfillmentInstructions() {
        return "受取番号" + pickupNumber + "で受け渡し";
    }
}
