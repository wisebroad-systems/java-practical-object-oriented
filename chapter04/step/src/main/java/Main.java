public class Main {
    public static void main(String[] args) {
        Order cashOrder = createOrder();
        pay(cashOrder, PaymentType.CASH, 3000, null, null);
        System.out.println("現金：" + cashOrder.getStatus());

        Order cardOrder = createOrder();
        pay(cardOrder, PaymentType.CREDIT_CARD, 0, "card-0001", null);
        System.out.println("カード：" + cardOrder.getStatus());

        Order qrOrder = createOrder();
        pay(qrOrder, PaymentType.QR, 0, null, "qr-0001");
        System.out.println("QRコード：" + qrOrder.getStatus());

        Order freeOrder = new Order();
        freeOrder.addItem(new MenuItem("お冷や", 0), 1);
        freeOrder.confirm();
        pay(freeOrder, PaymentType.CREDIT_CARD, 0, "card-0001", null);
        System.out.println("無料の注文：" + freeOrder.getStatus());
    }

    private static Order createOrder() {
        Order order = new Order();
        order.addItem(new MenuItem("コーヒー", 400), 2);
        order.addItem(new MenuItem("ハンバーグ", 1200), 1);
        order.confirm();
        return order;
    }

    private static boolean pay(
            Order order, PaymentType type, int receivedAmount, String cardToken, String code) {
        order.checkPayable();
        int amount = order.totalPrice();
        if (amount == 0) {
            order.markPaid();
            return true;
        }

        boolean succeeded;
        switch (type) {
            case CASH:
                succeeded = receivedAmount >= amount;
                if (succeeded) {
                    System.out.println("お釣り：" + (receivedAmount - amount) + "円");
                }
                break;
            case CREDIT_CARD:
                // カード（cardToken）の承認の依頼の代わり（第8章で外部の決済サービスとのやり取りに進む）
                succeeded = true;
                break;
            case QR:
                // 決済コード（code）を使った支払いの依頼の代わり（第8章で外部の決済サービスとのやり取りに進む）
                succeeded = true;
                break;
            default:
                throw new IllegalArgumentException("対応していない支払い方法です：" + type);
        }

        if (succeeded) {
            order.markPaid();
        }
        return succeeded;
    }
}
