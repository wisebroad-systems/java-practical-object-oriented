public class MenuItem {
    private final String name;
    private final Money price;

    public MenuItem(String name, Money price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前は空にできません");
        }
        if (price == null) {
            throw new IllegalArgumentException("価格はnullにできません");
        }
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }
}
