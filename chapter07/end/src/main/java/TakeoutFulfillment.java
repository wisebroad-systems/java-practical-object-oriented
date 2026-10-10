public class TakeoutFulfillment implements FulfillmentMethod {
    private final int pickupNumber;

    public TakeoutFulfillment(int pickupNumber) {
        if (pickupNumber < 1) {
            throw new IllegalArgumentException("受取番号は1以上にしてください");
        }
        this.pickupNumber = pickupNumber;
    }

    @Override
    public String instructions() {
        return "受取番号" + pickupNumber + "で受け渡し";
    }
}
