public final class Quantity {
    private final int value;

    public Quantity(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("数量は1以上にしてください");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Quantity)) {
            return false;
        }
        Quantity quantity = (Quantity) other;
        return value == quantity.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
