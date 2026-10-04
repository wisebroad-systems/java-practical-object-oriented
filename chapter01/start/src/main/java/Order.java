import java.util.ArrayList;
import java.util.List;

public class Order {
    private final List<MenuItem> items = new ArrayList<>();

    public List<MenuItem> getItems() {
        return items;
    }
}
