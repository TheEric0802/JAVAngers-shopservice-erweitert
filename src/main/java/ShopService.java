import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
public class ShopService {
    private final ProductRepo productRepo;
    private final OrderRepo orderRepo;
    private final IdService idService;

    public ShopService() {
        this.productRepo = new ProductRepo();
        this.orderRepo = new OrderListRepo();
        this.idService = new IdService();
    }

    public Order addOrder(List<String> productIds) throws NoSuchElementException {
        List<Product> products = new ArrayList<>();
        for (String productId : productIds) {
            Optional<Product> productToOrder = productRepo.getProductById(productId);

            if (productToOrder.isEmpty()) {
                throw new NoSuchElementException("Product mit der Id: " + productId + " konnte nicht bestellt werden!");
            }
            products.add(productToOrder.get());
        }

        Order newOrder = new Order(idService.generateId(), products, OrderStatus.PROCESSING, Instant.now());

        return orderRepo.addOrder(newOrder);
    }

    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepo.getOrders().stream()
                .filter(order -> order.status() == status)
                .toList();
    }

    public void updateOrder(String id, OrderStatus newStatus) {
        orderRepo.updateOrderStatus(id, newStatus);
    }

    public Map<OrderStatus, Order> getOldestOrderPerStatus() {
        Map<OrderStatus, Order> result = new HashMap<>();
        for (OrderStatus status : OrderStatus.values()) {
            getOrdersByStatus(status).stream().min(Comparator.comparing(Order::timestamp)).ifPresent(order -> result.put(status, order));
        }
        return result;
    }
}
