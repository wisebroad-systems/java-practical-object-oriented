import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DineInAndTakeoutOrderTest {

    @Test
    @DisplayName("店内の注文は、テーブル番号で配膳先を案内する")
    void dineInOrderGivesInstructions() {
        assertEquals("テーブル3へ配膳", new DineInOrder(3).fulfillmentInstructions());
    }

    @Test
    @DisplayName("テイクアウトの注文は、受取番号で受け渡しを案内する")
    void takeoutOrderGivesInstructions() {
        assertEquals("受取番号12で受け渡し", new TakeoutOrder(12).fulfillmentInstructions());
    }

    @Test
    @DisplayName("テーブル番号・受取番号は1以上でなければならない")
    void rejectsInvalidNumbers() {
        assertThrows(IllegalArgumentException.class, () -> new DineInOrder(0));
        assertThrows(IllegalArgumentException.class, () -> new TakeoutOrder(-1));
    }

    private static OrderStatus payWith(Order order, PaymentMethod method) {
        order.addItem(new MenuItem("コーヒー", 400), 2);
        order.addItem(new MenuItem("ハンバーグ", 1200), 1);
        order.confirm();
        order.pay(method);
        return order.getStatus();
    }

    @Test
    @DisplayName("店内でもテイクアウトでも、現金・カード・QRコードのどれでも支払える")
    void anyPaymentMethodWorksForEachFulfillment() {
        assertEquals(OrderStatus.PAID, payWith(new DineInOrder(3), new CashPayment(3000)));
        assertEquals(OrderStatus.PAID, payWith(new DineInOrder(3), new CardPayment("card-0001")));
        assertEquals(OrderStatus.PAID, payWith(new DineInOrder(3), new QrPayment("qr-0001")));
        assertEquals(OrderStatus.PAID, payWith(new TakeoutOrder(12), new CashPayment(3000)));
        assertEquals(OrderStatus.PAID, payWith(new TakeoutOrder(12), new CardPayment("card-0001")));
        assertEquals(OrderStatus.PAID, payWith(new TakeoutOrder(12), new QrPayment("qr-0001")));
    }
}
