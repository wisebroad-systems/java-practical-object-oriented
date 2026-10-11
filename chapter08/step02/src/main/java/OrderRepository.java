public interface OrderRepository {
    void save(Order order);

    Order getById(OrderId id);
}
