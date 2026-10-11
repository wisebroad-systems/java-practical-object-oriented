// 外部の決済サービスの代わり。実際には通信せず、依頼の内容を表示して成功を返す
public class FakePaymentGateway {
    public boolean payByCard(String cardToken, Money amount) {
        if (cardToken == null) {
            throw new IllegalArgumentException("カードの識別情報はnullにできません");
        }
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        System.out.println("決済の依頼：カード（" + cardToken + "）、" + amount.getYen() + "円");
        return true;
    }

    public boolean payByQr(String paymentCode, Money amount) {
        if (paymentCode == null) {
            throw new IllegalArgumentException("決済コードはnullにできません");
        }
        if (amount == null) {
            throw new IllegalArgumentException("支払う金額はnullにできません");
        }
        System.out.println("決済の依頼：QRコード（" + paymentCode + "）、" + amount.getYen() + "円");
        return true;
    }
}
