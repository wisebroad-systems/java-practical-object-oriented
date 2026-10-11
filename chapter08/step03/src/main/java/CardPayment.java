public class CardPayment implements PaymentMethod {
    private final String cardToken;
    private final FakePaymentGateway gateway = new FakePaymentGateway();

    public CardPayment(String cardToken) {
        if (cardToken == null || cardToken.isBlank()) {
            throw new IllegalArgumentException("カードの識別情報は空にできません");
        }
        this.cardToken = cardToken;
    }

    @Override
    public boolean pay(Money amount) {
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        return gateway.payByCard(cardToken, amount);
    }
}
