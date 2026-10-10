import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentMethodTest {

    @Test
    @DisplayName("現金：預かり金額が足りれば成功し、お釣りを求める")
    void cashPaysWithChange() {
        CashPayment cash = new CashPayment(new Money(3000));

        assertTrue(cash.pay(new Money(2000)));
        assertEquals(new Money(1000), cash.getChange());
    }

    @Test
    @DisplayName("現金：預かり金額と支払額が同じなら成功し、お釣りは0円")
    void cashPaysExactAmount() {
        CashPayment cash = new CashPayment(new Money(2000));

        assertTrue(cash.pay(new Money(2000)));
        assertEquals(new Money(0), cash.getChange());
    }

    @Test
    @DisplayName("現金：預かり金額が足りなければ失敗し、お釣りは取得できない")
    void cashFailsWhenInsufficient() {
        CashPayment cash = new CashPayment(new Money(1000));

        assertFalse(cash.pay(new Money(2000)));
        assertThrows(IllegalStateException.class, cash::getChange);
    }

    @Test
    @DisplayName("現金：支払う前にお釣りは取得できない")
    void changeIsUnavailableBeforePayment() {
        assertThrows(
                IllegalStateException.class, () -> new CashPayment(new Money(3000)).getChange());
    }

    @Test
    @DisplayName("現金：預かり金額がnullなら拒否する。0円は預かれる")
    void rejectsNullReceivedAmount() {
        assertThrows(IllegalArgumentException.class, () -> new CashPayment(null));
        assertTrue(new CashPayment(new Money(0)).pay(new Money(0)));
    }

    @Test
    @DisplayName("現金・カード・QRコード：支払う金額がnullなら拒否する")
    void rejectsNullAmount() {
        assertThrows(
                IllegalArgumentException.class, () -> new CashPayment(new Money(3000)).pay(null));
        assertThrows(IllegalArgumentException.class, () -> new CardPayment("card-0001").pay(null));
        assertThrows(IllegalArgumentException.class, () -> new QrPayment("qr-0001").pay(null));
    }

    @Test
    @DisplayName("カード・QRコード：識別情報があれば支払いを依頼して成功する")
    void cardAndQrPay() {
        assertTrue(new CardPayment("card-0001").pay(new Money(2000)));
        assertTrue(new QrPayment("qr-0001").pay(new Money(2000)));
    }

    @Test
    @DisplayName("カード・QRコード：識別情報がnull・空・空白だけなら拒否する")
    void rejectsBlankIdentifiers() {
        assertThrows(IllegalArgumentException.class, () -> new CardPayment(null));
        assertThrows(IllegalArgumentException.class, () -> new CardPayment(" "));
        assertThrows(IllegalArgumentException.class, () -> new QrPayment(null));
        assertThrows(IllegalArgumentException.class, () -> new QrPayment(""));
    }
}
