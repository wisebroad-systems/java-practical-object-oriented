public class RejectionExamples {
    public static void main(String[] args) {
        Order draftOrder = new Order();
        draftOrder.addItem(new MenuItem("コーヒー", 400), 1);
        try {
            draftOrder.pay(new CardPayment("card-0001"));
        } catch (IllegalStateException e) {
            System.out.println("未確定の注文の支払い：" + e.getMessage());
        }
        System.out.println("状態：" + draftOrder.getStatus());

        Order paidOrder = new Order();
        paidOrder.addItem(new MenuItem("コーヒー", 400), 1);
        paidOrder.confirm();
        paidOrder.pay(new CardPayment("card-0001"));
        try {
            paidOrder.pay(new CardPayment("card-0001"));
        } catch (IllegalStateException e) {
            System.out.println("支払い済みの注文の再支払い：" + e.getMessage());
        }
        System.out.println("状態：" + paidOrder.getStatus());
    }
}
