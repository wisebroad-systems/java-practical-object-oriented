public class NoDiscountPolicy implements DiscountPolicy {
    @Override
    public Money calculateDiscount(Money totalPrice) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("合計はnullにできません");
        }
        return new Money(0);
    }
}
