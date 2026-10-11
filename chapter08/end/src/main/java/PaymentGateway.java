public interface PaymentGateway {
    boolean payByCard(String cardToken, Money amount);

    boolean payByQr(String paymentCode, Money amount);
}
