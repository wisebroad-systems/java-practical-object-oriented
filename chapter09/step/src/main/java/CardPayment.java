public class CardPayment implements PaymentMethod {
    private final String cardToken;
    private final PaymentGateway gateway;

    public CardPayment(String cardToken, PaymentGateway gateway) {
        if (cardToken == null || cardToken.isBlank()) {
            throw new IllegalArgumentException("カードの識別情報は空にできません");
        }
        if (gateway == null) {
            throw new IllegalArgumentException("決済の窓口はnullにできません");
        }
        this.cardToken = cardToken;
        this.gateway = gateway;
    }

    @Override
    public boolean pay(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        return gateway.payByCard(cardToken, amount);
    }
}
