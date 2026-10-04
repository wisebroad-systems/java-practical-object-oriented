import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    private final List<MenuItem> items = new ArrayList<>();
    private boolean confirmed = false;

    public void addItem(MenuItem item) {
        if (confirmed) {
            throw new IllegalStateException("確定後は料理を追加できません");
        }
        if (item == null) {
            throw new IllegalArgumentException("料理はnullにできません");
        }
        items.add(item);
    }

    public void confirm() {
        if (confirmed) {
            throw new IllegalStateException("注文はすでに確定しています");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("空の注文は確定できません");
        }
        confirmed = true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public List<MenuItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public int totalPrice() {
        int total = 0;
        for (MenuItem item : items) {
            total += item.getPrice();
        }
        return total;
    }
}
