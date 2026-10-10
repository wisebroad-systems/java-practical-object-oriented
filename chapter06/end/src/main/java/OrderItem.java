public class OrderItem {
    private final MenuItem menuItem;
    private final Quantity quantity;

    public OrderItem(MenuItem menuItem, Quantity quantity) {
        if (menuItem == null) {
            throw new IllegalArgumentException("料理はnullにできません");
        }
        if (quantity == null) {
            throw new IllegalArgumentException("数量はnullにできません");
        }
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public Money subtotal() {
        return menuItem.getPrice().multiply(quantity);
    }
}
