public class MenuItem {
    private final String name;
    private final int price;

    public MenuItem(String name, int price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("名前は空にできません");
        }
        if (price < 0) {
            throw new IllegalArgumentException("価格は0以上にしてください");
        }
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}
