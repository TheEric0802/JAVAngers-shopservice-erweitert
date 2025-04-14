import java.util.List;

public class Main {

    public static void main(String[] args) {
        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();
        ShopService shopService = new ShopService(productRepo, orderRepo);

        productRepo.addProduct(new Product("2", "Birne"));
        productRepo.addProduct(new Product("3", "Erdbeere"));
        productRepo.addProduct(new Product("4", "Kirsche"));

        shopService.addOrder(List.of("1", "3"));
        shopService.addOrder(List.of("2", "4"));
        shopService.addOrder(List.of("1", "2", "3", "4"));

        System.out.println(shopService.getOrdersByStatus(OrderStatus.PROCESSING));
    }
}
