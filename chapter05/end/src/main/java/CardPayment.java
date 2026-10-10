public class CardPayment implements PaymentMethod {
    private final String cardToken;

    public CardPayment(String cardToken) {
        if (cardToken == null || cardToken.isBlank()) {
            throw new IllegalArgumentException("カードの識別情報は空にできません");
        }
        this.cardToken = cardToken;
    }

    @Override
    public boolean pay(int amount) {
        return requestApproval(amount);
    }

    private boolean requestApproval(int amount) {
        // カード会社への承認の依頼の代わり（第8章で外部の決済サービスとのやり取りに進む）
        return true;
    }
}
