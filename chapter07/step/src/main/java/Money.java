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

    public Money subtract(Money other) {
        if (other == null) {
            throw new IllegalArgumentException("引く金額はnullにできません");
        }
        if (other.yen > yen) {
            throw new IllegalArgumentException("引いた結果が負の金額になります");
        }
        return new Money(yen - other.yen);
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
