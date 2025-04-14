import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        ProductRepo productRepo = new ProductRepo();
        OrderRepo orderRepo = new OrderMapRepo();
        IdService idService = new IdService();
        ShopService shopService = new ShopService(productRepo, orderRepo, idService);

        productRepo.addProduct(new Product("2", "Birne"));
        productRepo.addProduct(new Product("3", "Erdbeere"));
        productRepo.addProduct(new Product("4", "Kirsche"));

        shopService.addOrder(List.of("1", "3"));
        shopService.addOrder(List.of("2", "4"));
        shopService.addOrder(List.of("1", "2", "3", "4"));

        System.out.println(shopService.getOrdersByStatus(OrderStatus.PROCESSING));

        System.out.println("------------------------");

        ProductRepo productRepo2 = new ProductRepo();
        productRepo2.addProduct(new Product("1", "Apfel"));
        productRepo2.addProduct(new Product("2", "Birne"));
        productRepo2.addProduct(new Product("3", "Erdbeere"));
        productRepo2.addProduct(new Product("4", "Kirsche"));
        OrderRepo orderRepo2 = new OrderMapRepo();
        IdService idService2 = new IdService();
        ShopService shopService2 = new ShopService(productRepo2, orderRepo2, idService2);
        Map<String, String> aliasMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader("src/transactions.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                switch (parts[0]) {
                    case "addOrder":
                        String alias = parts[1];
                        List<String> productIds = Arrays.stream(parts).skip(2).toList();
                        aliasMap.put(alias, shopService2.addOrder(productIds).id());
                        break;
                    case "setStatus":
                        shopService2.updateOrder(aliasMap.get(parts[1]), OrderStatus.valueOf(parts[2]));
                        break;
                    case "printOrders":
                        for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
                            System.out.println("Order: " + entry.getKey());
                            System.out.println(orderRepo2.getOrderById(entry.getValue()));
                        }
                        break;
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
