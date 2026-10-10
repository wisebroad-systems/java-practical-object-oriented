public interface DiscountPolicy {
    Money calculateDiscount(Money totalPrice);
}
