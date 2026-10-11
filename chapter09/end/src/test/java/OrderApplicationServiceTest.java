import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class OrderApplicationServiceTest {

    /**
     * 保存・取得と支払いの依頼を、起きた順に1つの記録へ書き出す（テスト用）。
     *
     * <p>メモリ上の保存は同じ注文への参照を持つので、保存した時点の状態と支払額を文字列に写して記録する。
     */
    private static class EventLog {
        private final List<String> events = new ArrayList<>();
    }

    private static class RecordingRepository implements OrderRepository {
        private final InMemoryOrderRepository repository = new InMemoryOrderRepository();
        private final EventLog log;

        RecordingRepository(EventLog log) {
            this.log = log;
        }

        @Override
        public void save(Order order) {
            log.events.add(
                    "save:"
                            + order.getId().getValue()
                            + ":"
                            + order.getStatus()
                            + ":"
                            + order.getAmountDue().getYen());
            repository.save(order);
        }

        @Override
        public Order getById(OrderId id) {
            log.events.add("get:" + id.getValue());
            return repository.getById(id);
        }
    }

    private static class RecordingPayment implements PaymentMethod {
        private final EventLog log;
        private final boolean result;

        RecordingPayment(EventLog log, boolean result) {
            this.log = log;
            this.result = result;
        }

        @Override
        public boolean pay(Money amount) {
            log.events.add("pay:" + amount.getYen());
            return result;
        }
    }

    /** 割引額の計算を依頼された回数を数える割引方針（テスト用）。 */
    private static class CountingPolicy implements DiscountPolicy {
        private final DiscountPolicy policy;
        private int calls = 0;

        CountingPolicy(DiscountPolicy policy) {
            this.policy = policy;
        }

        @Override
        public Money calculateDiscount(Money totalPrice) {
            calls++;
            return policy.calculateDiscount(totalPrice);
        }
    }

    private static Order order(String id, int price, int quantity) {
        Order order = new Order(new OrderId(id), new DineInFulfillment(3));
        order.addItem(new MenuItem("料理", new Money(price)), new Quantity(quantity));
        return order;
    }

    private static Order standardOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        return order;
    }

    @Test
    @DisplayName("注文の保存先はnullにできない")
    void rejectsNullRepository() {
        IllegalArgumentException e =
                assertThrows(
                        IllegalArgumentException.class, () -> new OrderApplicationService(null));
        assertEquals("注文の保存先はnullにできません", e.getMessage());
    }

    @Test
    @DisplayName("初回は、確定して未払いで保存してから支払い、成功したら支払い済みで保存し直す")
    void confirmAndPaySavesBeforeAndAfterPayment() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        Order order = standardOrder();

        assertTrue(
                service.confirmAndPay(
                        order, new CouponDiscountPolicy(), new RecordingPayment(log, true)));
        assertEquals(
                List.of("save:order-001:CONFIRMED:1700", "pay:1700", "save:order-001:PAID:1700"),
                log.events);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("初回の決済が失敗したら、falseを返し、未払いのまま保存し直さない")
    void confirmAndPayKeepsUnpaidOrderOnFailure() {
        EventLog log = new EventLog();
        RecordingRepository repository = new RecordingRepository(log);
        OrderApplicationService service = new OrderApplicationService(repository);

        assertFalse(
                service.confirmAndPay(
                        standardOrder(),
                        new CouponDiscountPolicy(),
                        new RecordingPayment(log, false)));
        assertEquals(List.of("save:order-001:CONFIRMED:1700", "pay:1700"), log.events);
        Order saved = repository.getById(new OrderId("order-001"));
        assertEquals(OrderStatus.CONFIRMED, saved.getStatus());
        assertEquals(new Money(1700), saved.getAmountDue());
    }

    @Test
    @DisplayName("支払額が0円なら、決済を依頼せずに支払い済みで保存し、trueを返す")
    void confirmAndPayCompletesFreeOrderWithoutPayment() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        Order order = order("order-002", 200, 1);

        assertTrue(
                service.confirmAndPay(
                        order, new CouponDiscountPolicy(), new RecordingPayment(log, true)));
        assertEquals(List.of("save:order-002:CONFIRMED:0", "save:order-002:PAID:0"), log.events);
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    @DisplayName("初回の必須の引数がnullなら、確定・保存・決済を始めない")
    void confirmAndPayRejectsNullArgumentsBeforeStarting() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        Order order = standardOrder();
        RecordingPayment payment = new RecordingPayment(log, true);

        IllegalArgumentException noOrder =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.confirmAndPay(null, new NoDiscountPolicy(), payment));
        assertEquals("注文はnullにできません", noOrder.getMessage());
        IllegalArgumentException noPolicy =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.confirmAndPay(order, null, payment));
        assertEquals("割引方針はnullにできません", noPolicy.getMessage());
        IllegalArgumentException noPayment =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.confirmAndPay(order, new NoDiscountPolicy(), null));
        assertEquals("支払い方法はnullにできません", noPayment.getMessage());

        assertEquals(OrderStatus.DRAFT, order.getStatus());
        assertTrue(log.events.isEmpty());
    }

    @Test
    @DisplayName("確定済みの注文を渡すと、状態の違反をfalseに変えずに例外で知らせ、保存・決済しない")
    void confirmAndPayDoesNotHideStateViolation() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        Order order = standardOrder();
        order.confirm();

        IllegalStateException e =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.confirmAndPay(
                                        order,
                                        new CouponDiscountPolicy(),
                                        new RecordingPayment(log, true)));
        assertEquals("注文はすでに確定しています", e.getMessage());
        assertTrue(log.events.isEmpty());
        assertEquals(new Money(2000), order.getAmountDue());
    }

    @Test
    @DisplayName("再試行は、保存された注文を取得し、確定時に固定した金額で支払い、成功したら保存する")
    void retryPaymentUsesFixedAmount() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        CountingPolicy policy = new CountingPolicy(new CouponDiscountPolicy());
        service.confirmAndPay(standardOrder(), policy, new RecordingPayment(log, false));
        log.events.clear();

        assertTrue(service.retryPayment(new OrderId("order-001"), new RecordingPayment(log, true)));
        assertEquals(List.of("get:order-001", "pay:1700", "save:order-001:PAID:1700"), log.events);
        assertEquals(1, policy.calls);
    }

    @Test
    @DisplayName("再試行の決済も失敗したら、falseを返し、未払いのまま保存し直さない")
    void retryPaymentKeepsUnpaidOrderOnFailure() {
        EventLog log = new EventLog();
        RecordingRepository repository = new RecordingRepository(log);
        OrderApplicationService service = new OrderApplicationService(repository);
        service.confirmAndPay(
                standardOrder(), new CouponDiscountPolicy(), new RecordingPayment(log, false));
        log.events.clear();

        assertFalse(
                service.retryPayment(new OrderId("order-001"), new RecordingPayment(log, false)));
        assertEquals(List.of("get:order-001", "pay:1700"), log.events);
        Order saved = repository.getById(new OrderId("order-001"));
        assertEquals(OrderStatus.CONFIRMED, saved.getStatus());
    }

    @Test
    @DisplayName("再試行の必須の引数がnullなら、取得・決済を始めない")
    void retryPaymentRejectsNullArgumentsBeforeStarting() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));

        IllegalArgumentException noId =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.retryPayment(null, new RecordingPayment(log, true)));
        assertEquals("注文IDはnullにできません", noId.getMessage());
        IllegalArgumentException noPayment =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.retryPayment(new OrderId("order-001"), null));
        assertEquals("支払い方法はnullにできません", noPayment.getMessage());
        assertTrue(log.events.isEmpty());
    }

    @Test
    @DisplayName("保存されていない注文IDの再試行は、決済を呼ばずに例外で知らせる")
    void retryPaymentRejectsUnknownOrder() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));

        NoSuchElementException e =
                assertThrows(
                        NoSuchElementException.class,
                        () ->
                                service.retryPayment(
                                        new OrderId("order-999"), new RecordingPayment(log, true)));
        assertEquals("注文が見つかりません：order-999", e.getMessage());
        assertEquals(List.of("get:order-999"), log.events);
    }

    @Test
    @DisplayName("支払い済みの注文の再試行は、決済を呼ばずに例外で知らせ、支払い済みのまま")
    void retryPaymentRejectsPaidOrder() {
        EventLog log = new EventLog();
        RecordingRepository repository = new RecordingRepository(log);
        OrderApplicationService service = new OrderApplicationService(repository);
        service.confirmAndPay(
                standardOrder(), new CouponDiscountPolicy(), new RecordingPayment(log, true));
        log.events.clear();

        IllegalStateException e =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.retryPayment(
                                        new OrderId("order-001"), new RecordingPayment(log, true)));
        assertEquals("確定済み・未払いの注文だけ支払えます", e.getMessage());
        assertEquals(List.of("get:order-001"), log.events);
        assertEquals(OrderStatus.PAID, repository.getById(new OrderId("order-001")).getStatus());
    }

    @Test
    @DisplayName("現金でも、同じ操作で確定・支払い・保存ができる")
    void worksWithCashPayment() {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        OrderApplicationService service = new OrderApplicationService(repository);
        Order order = standardOrder();

        assertTrue(
                service.confirmAndPay(
                        order, new NoDiscountPolicy(), new CashPayment(new Money(2000))));
        assertSame(order, repository.getById(new OrderId("order-001")));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }
}
