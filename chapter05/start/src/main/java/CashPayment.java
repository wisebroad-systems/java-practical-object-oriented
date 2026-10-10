public class CashPayment implements PaymentMethod {
    private final int receivedAmount;
    private int change;
    private boolean paid = false;

    public CashPayment(int receivedAmount) {
        if (receivedAmount < 0) {
            throw new IllegalArgumentException("預かり金額は0以上にしてください");
        }
        this.receivedAmount = receivedAmount;
    }

    @Override
    public boolean pay(int amount) {
        if (receivedAmount < amount) {
            return false;
        }
        change = receivedAmount - amount;
        paid = true;
        return true;
    }

    public int getChange() {
        if (!paid) {
            throw new IllegalStateException("支払いが済んでいないため、お釣りはありません");
        }
        return change;
    }
}
