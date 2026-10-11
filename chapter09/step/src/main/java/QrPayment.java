public class QrPayment implements PaymentMethod {
    private final String paymentCode;
    private final PaymentGateway gateway;

    public QrPayment(String paymentCode, PaymentGateway gateway) {
        if (paymentCode == null || paymentCode.isBlank()) {
            throw new IllegalArgumentException("決済コードは空にできません");
        }
        if (gateway == null) {
            throw new IllegalArgumentException("決済の窓口はnullにできません");
        }
        this.paymentCode = paymentCode;
        this.gateway = gateway;
    }

    @Override
    public boolean pay(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        return gateway.payByQr(paymentCode, amount);
    }
}
