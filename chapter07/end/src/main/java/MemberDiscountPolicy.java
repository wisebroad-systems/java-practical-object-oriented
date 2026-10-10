public class MemberDiscountPolicy implements DiscountPolicy {
    @Override
    public Money calculateDiscount(Money totalPrice) {
        if (totalPrice == null) {
            throw new IllegalArgumentException("合計はnullにできません");
        }
        // 明細合計の10％。1円未満は切り捨てる
        return new Money(totalPrice.getYen() / 10);
    }
}
