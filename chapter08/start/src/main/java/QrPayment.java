public class QrPayment implements PaymentMethod {
    private final String paymentCode;

    public QrPayment(String paymentCode) {
        if (paymentCode == null || paymentCode.isBlank()) {
            throw new IllegalArgumentException("決済コードは空にできません");
        }
        this.paymentCode = paymentCode;
    }

    @Override
    public boolean pay(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        return requestPayment(amount);
    }

    private boolean requestPayment(Money amount) {
        // 決済コードを使った支払いの依頼の代わり（第8章で外部の決済サービスとのやり取りに進む）
        return true;
    }
}
