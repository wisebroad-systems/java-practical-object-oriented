public final class Money {
    private final int yen;

    public Money(int yen) {
        if (yen < 0) {
            throw new IllegalArgumentException("金額は0以上にしてください");
        }
        this.yen = yen;
    }

    public int getYen() {
        return yen;
    }

    public Money add(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("加える金額はnullにできません");
        }
        return new Money(Math.addExact(yen, other.yen));
    }

    public Money multiply(Quantity quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("数量はnullにできません");
        }
        return new Money(Math.multiplyExact(yen, quantity.getValue()));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Money)) {
            return false;
        }
        Money money = (Money) other;
        return yen == money.yen;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(yen);
    }
}
