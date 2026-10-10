public class CouponDiscountPolicy implements DiscountPolicy {
    @Override
    public Money calculateDiscount(Money totalPrice) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("合計はnullにできません");
        }
        // 300円。明細合計が300円より少なければ、明細合計と同じ額
        if (totalPrice.getYen() < 300) {
            return totalPrice;
        }
        return new Money(300);
    }
}
