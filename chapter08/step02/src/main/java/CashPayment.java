public class CashPayment implements PaymentMethod {
    private final Money receivedAmount;
    private Money change;
    private boolean paid = false;

    public CashPayment(Money receivedAmount) {
        if (receivedAmount == null) {
            throw new IllegalArgumentException("預かり金額はnullにできません");
        }
        this.receivedAmount = receivedAmount;
    }

    @Override
    public boolean pay(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        if (receivedAmount.getYen() < amount.getYen()) {
            return false;
        }
        change = new Money(receivedAmount.getYen() - amount.getYen());
        paid = true;
        return true;
    }

    public Money getChange() {
        if (!paid) {
            throw new IllegalStateException("支払いが済んでいないため、お釣りはありません");
        }
        return change;
    }
}
