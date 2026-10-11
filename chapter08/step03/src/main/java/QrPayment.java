public class QrPayment implements PaymentMethod {
    private final String paymentCode;
    private final FakePaymentGateway gateway = new FakePaymentGateway();

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
        return gateway.payByQr(paymentCode, amount);
    }
}
