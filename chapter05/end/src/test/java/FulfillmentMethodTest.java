import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FulfillmentMethodTest {

    @Test
    @DisplayName("店内の提供方法は、テーブル番号で配膳先を案内する")
    void dineInGivesInstructions() {
        assertEquals("テーブル3へ配膳", new DineInFulfillment(3).instructions());
    }

    @Test
    @DisplayName("テイクアウトの提供方法は、受取番号で受け渡しを案内する")
    void takeoutGivesInstructions() {
        assertEquals("受取番号12で受け渡し", new TakeoutFulfillment(12).instructions());
    }

    @Test
    @DisplayName("テーブル番号・受取番号は1以上でなければならない")
    void rejectsInvalidNumbers() {
        assertThrows(IllegalArgumentException.class, () -> new DineInFulfillment(0));
        assertThrows(IllegalArgumentException.class, () -> new TakeoutFulfillment(-1));
    }

    @Test
    @DisplayName("注文は、持っている提供方法に案内を任せる")
    void orderDelegatesInstructions() {
        assertEquals("テーブル3へ配膳", new Order(new DineInFulfillment(3)).fulfillmentInstructions());
        assertEquals(
                "受取番号12で受け渡し", new Order(new TakeoutFulfillment(12)).fulfillmentInstructions());
    }

    @Test
    @DisplayName("提供方法のない注文は作れない")
    void rejectsOrderWithoutFulfillment() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null));
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
        assertEquals(
                OrderStatus.PAID,
                payWith(new Order(new DineInFulfillment(3)), new CashPayment(3000)));
        assertEquals(
                OrderStatus.PAID,
                payWith(new Order(new DineInFulfillment(3)), new CardPayment("card-0001")));
        assertEquals(
                OrderStatus.PAID,
                payWith(new Order(new DineInFulfillment(3)), new QrPayment("qr-0001")));
        assertEquals(
                OrderStatus.PAID,
                payWith(new Order(new TakeoutFulfillment(12)), new CashPayment(3000)));
        assertEquals(
                OrderStatus.PAID,
                payWith(new Order(new TakeoutFulfillment(12)), new CardPayment("card-0001")));
        assertEquals(
                OrderStatus.PAID,
                payWith(new Order(new TakeoutFulfillment(12)), new QrPayment("qr-0001")));
    }
}
