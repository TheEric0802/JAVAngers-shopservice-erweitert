import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class ShopServiceTest {

    @Test
    void addOrderTest() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1");

        //WHEN
        Order actual = shopService.addOrder(productsIds);

        //THEN
        Order expected = new Order("-1", List.of(new Product("1", "Apfel")), OrderStatus.PROCESSING, Instant.now());
        assertEquals(expected.products(), actual.products());
        assertNotNull(expected.id());
    }

    @Test
    void addOrderTest_whenInvalidProductId_expectNoSuchElementException() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1", "2");

        //WHEN

        //THEN
        assertThrows(NoSuchElementException.class, () -> shopService.addOrder(productsIds));
    }

    @Test
    void getOrdersByStatusTest() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1");
        Order order = shopService.addOrder(productsIds);

        //WHEN
        List<Order> processing = shopService.getOrdersByStatus(OrderStatus.PROCESSING);
        List<Order> completed = shopService.getOrdersByStatus(OrderStatus.COMPLETED);

        //THEN
        assertAll(
                () -> assertEquals(List.of(order), processing),
                () -> assertTrue(completed.isEmpty())
        );
    }

    @Test
    void updateOrderTest() {
        //GIVEN
        ShopService shopService = new ShopService();
        List<String> productsIds = List.of("1");
        Order order = shopService.addOrder(productsIds);

        //WHEN
        shopService.updateOrder(order.id(), OrderStatus.COMPLETED);

        //THEN
        assertEquals(List.of(order.withStatus(OrderStatus.COMPLETED)), shopService.getOrdersByStatus(OrderStatus.COMPLETED));
    }

    @Test
    void getOldestOrderPerStatusTest() {
        // GIVEN
        ShopService shopService = new ShopService();
        Order oldProcessing = shopService.addOrder(List.of("1"));
        Order newProcessing = shopService.addOrder(List.of("1"));
        Order oldInDelivery = shopService.addOrder(List.of("1"));
        Order newInDelivery = shopService.addOrder(List.of("1"));
        Order oldCompleted = shopService.addOrder(List.of("1"));
        Order newCompleted = shopService.addOrder(List.of("1"));
        shopService.updateOrder(oldInDelivery.id(), OrderStatus.IN_DELIVERY);
        shopService.updateOrder(newInDelivery.id(), OrderStatus.IN_DELIVERY);
        shopService.updateOrder(oldCompleted.id(), OrderStatus.COMPLETED);
        shopService.updateOrder(newCompleted.id(), OrderStatus.COMPLETED);

        // WHEN
        Map<OrderStatus, Order> result = shopService.getOldestOrderPerStatus();

        // THEN
        assertAll(
            () -> assertEquals(oldProcessing, result.get(OrderStatus.PROCESSING)),
            () -> assertEquals(oldInDelivery.withStatus(OrderStatus.IN_DELIVERY), result.get(OrderStatus.IN_DELIVERY)),
            () -> assertEquals(oldCompleted.withStatus(OrderStatus.COMPLETED), result.get(OrderStatus.COMPLETED))
        );
    }

    @Test
    void getOldestOrderPerStatusTest_whenNoOrders_expectEmptyMap() {
        // GIVEN
        ShopService shopService = new ShopService();

        // WHEN
        Map<OrderStatus, Order> result = shopService.getOldestOrderPerStatus();

        // THEN
        assertTrue(result.isEmpty());
    }
}
