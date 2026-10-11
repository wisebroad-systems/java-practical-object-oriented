import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
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
            String amount = "-";
            if (order.getStatus() == OrderStatus.AMOUNT_FIXED
                    || order.getStatus() == OrderStatus.PAID) {
                amount = String.valueOf(order.getAmountDue().getYen());
            }
            log.events.add(
                    "save:" + order.getId().getValue() + ":" + order.getStatus() + ":" + amount);
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

    private static final OrderId ORDER_ID = new OrderId("order-001");

    private static Order standardOrder() {
        Order order = new Order(new OrderId("order-001"), new DineInFulfillment(3));
        order.addItem(new MenuItem("コーヒー", new Money(400)), new Quantity(2));
        order.addItem(new MenuItem("ハンバーグ", new Money(1200)), new Quantity(1));
        return order;
    }

    @Test
    @DisplayName("内容の確定では、支払額を固定せずに保存して終わる。料理は追加できない")
    void confirmOrderSavesContentOnly() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        Order order = standardOrder();

        service.confirmOrder(order);

        assertEquals(List.of("save:order-001:CONTENT_CONFIRMED:-"), log.events);
        assertEquals(OrderStatus.CONTENT_CONFIRMED, order.getStatus());
        assertThrows(IllegalStateException.class, order::getAmountDue);
        assertThrows(
                IllegalStateException.class,
                () -> order.addItem(new MenuItem("プリン", new Money(200)), new Quantity(1)));
        assertEquals(new Money(2000), order.totalPrice());
    }

    @Test
    @DisplayName("内容の確定の後で割引を選び、支払額を固定して未払いで保存してから支払う")
    void fixAmountAndPaySavesBeforeAndAfterPayment() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        service.confirmOrder(standardOrder());
        log.events.clear();

        assertTrue(
                service.fixAmountAndPay(
                        ORDER_ID, new CouponDiscountPolicy(), new RecordingPayment(log, true)));
        assertEquals(
                List.of(
                        "get:order-001",
                        "save:order-001:AMOUNT_FIXED:1700",
                        "pay:1700",
                        "save:order-001:PAID:1700"),
                log.events);
    }

    @Test
    @DisplayName("決済が失敗したら、AMOUNT_FIXEDと1700円のまま。別の割引は選び直せない")
    void failureKeepsFixedAmount() {
        EventLog log = new EventLog();
        RecordingRepository repository = new RecordingRepository(log);
        OrderApplicationService service = new OrderApplicationService(repository);
        service.confirmOrder(standardOrder());
        CountingPolicy member = new CountingPolicy(new MemberDiscountPolicy());

        assertFalse(
                service.fixAmountAndPay(
                        ORDER_ID, new CouponDiscountPolicy(), new RecordingPayment(log, false)));
        log.events.clear();
        IllegalStateException e =
                assertThrows(
                        IllegalStateException.class,
                        () ->
                                service.fixAmountAndPay(
                                        ORDER_ID, member, new RecordingPayment(log, true)));
        assertEquals("支払額はすでに固定されています", e.getMessage());
        assertEquals(List.of("get:order-001"), log.events);
        assertEquals(0, member.calls);
        Order saved = repository.getById(ORDER_ID);
        assertEquals(OrderStatus.AMOUNT_FIXED, saved.getStatus());
        assertEquals(new Money(1700), saved.getAmountDue());
    }

    @Test
    @DisplayName("再試行は、割引を計算し直さずに固定した1700円で支払い、成功したら保存する")
    void retryPaymentUsesFixedAmount() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        CountingPolicy coupon = new CountingPolicy(new CouponDiscountPolicy());
        service.confirmOrder(standardOrder());
        service.fixAmountAndPay(ORDER_ID, coupon, new RecordingPayment(log, false));
        log.events.clear();

        assertTrue(service.retryPayment(ORDER_ID, new RecordingPayment(log, true)));
        assertEquals(List.of("get:order-001", "pay:1700", "save:order-001:PAID:1700"), log.events);
        assertEquals(1, coupon.calls);
    }

    @Test
    @DisplayName("支払額を固定する前の再試行は拒否し、決済を呼ばない")
    void retryBeforeFixingIsRejected() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        service.confirmOrder(standardOrder());
        log.events.clear();

        IllegalStateException e =
                assertThrows(
                        IllegalStateException.class,
                        () -> service.retryPayment(ORDER_ID, new RecordingPayment(log, true)));
        assertEquals("支払額を固定した未払いの注文だけ支払えます", e.getMessage());
        assertEquals(List.of("get:order-001"), log.events);
    }

    @Test
    @DisplayName("支払い済みの注文には、割引の適用も再試行もできず、決済を呼ばない")
    void paidOrderRejectsBothOperations() {
        EventLog log = new EventLog();
        RecordingRepository repository = new RecordingRepository(log);
        OrderApplicationService service = new OrderApplicationService(repository);
        service.confirmOrder(standardOrder());
        service.fixAmountAndPay(ORDER_ID, new NoDiscountPolicy(), new RecordingPayment(log, true));
        log.events.clear();

        assertThrows(
                IllegalStateException.class,
                () ->
                        service.fixAmountAndPay(
                                ORDER_ID,
                                new CouponDiscountPolicy(),
                                new RecordingPayment(log, true)));
        assertThrows(
                IllegalStateException.class,
                () -> service.retryPayment(ORDER_ID, new RecordingPayment(log, true)));
        assertEquals(List.of("get:order-001", "get:order-001"), log.events);
        Order saved = repository.getById(ORDER_ID);
        assertEquals(OrderStatus.PAID, saved.getStatus());
        assertEquals(new Money(2000), saved.getAmountDue());
    }

    @Test
    @DisplayName("200円の注文にクーポンを選ぶと支払額は0円で、決済を呼ばずに支払い済みで保存する")
    void freeOrderCompletesWithoutPayment() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        Order order = new Order(new OrderId("order-002"), new TakeoutFulfillment(12));
        order.addItem(new MenuItem("プリン", new Money(200)), new Quantity(1));
        service.confirmOrder(order);
        log.events.clear();

        assertTrue(
                service.fixAmountAndPay(
                        new OrderId("order-002"),
                        new CouponDiscountPolicy(),
                        new RecordingPayment(log, true)));
        assertEquals(
                List.of("get:order-002", "save:order-002:AMOUNT_FIXED:0", "save:order-002:PAID:0"),
                log.events);
    }

    @Test
    @DisplayName("必須の引数がnullなら、取得・確定・保存・決済を始めない")
    void rejectsNullArgumentsBeforeStarting() {
        EventLog log = new EventLog();
        OrderApplicationService service = new OrderApplicationService(new RecordingRepository(log));
        RecordingPayment payment = new RecordingPayment(log, true);

        assertThrows(IllegalArgumentException.class, () -> new OrderApplicationService(null));
        assertThrows(IllegalArgumentException.class, () -> service.confirmOrder(null));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.fixAmountAndPay(null, new NoDiscountPolicy(), payment));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.fixAmountAndPay(ORDER_ID, null, payment));
        assertThrows(
                IllegalArgumentException.class,
                () -> service.fixAmountAndPay(ORDER_ID, new NoDiscountPolicy(), null));
        assertThrows(IllegalArgumentException.class, () -> service.retryPayment(null, payment));
        assertThrows(IllegalArgumentException.class, () -> service.retryPayment(ORDER_ID, null));
        assertTrue(log.events.isEmpty());
    }
}
