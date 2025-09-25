import lib.*;

public class Test{

    private static int passedCount = 0;
    private static int failedCount = 0;

    /**
     * เมธอดเสริมสำหรับตรวจสอบเงื่อนไขและพิมพ์ผลลัพธ์
     * @param testName ชื่อของกรณีทดสอบ
     * @param condition เงื่อนไขที่ต้องเป็นจริงเพื่อให้เทสต์ผ่าน
     */
    private static void check(String testName, boolean condition) {
        if (condition) {
            System.out.println("PASSED: " + testName);
            passedCount++;
        } else {
            System.out.println("FAILED: " + testName);
            failedCount++;
        }
    }

    /**
     * จุดเริ่มต้นการทำงานของโปรแกรมทดสอบ
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("--- Starting Comprehensive E-commerce System Tests ---");
        
        // --- Setup ---
        Product littlePony = new Product("P001", "littlePony", 10.0);
        Product Immuna = new Product("P002", "Immuna", 5.0);
        Product Lava = new Product("P003", "Lava", 20.0);

        Productcatalog catalog = new Productcatalog();
        catalog.addProduct(littlePony);
        catalog.addProduct(Immuna);
        catalog.addProduct(Lava);

        PricingService pricingService = new PricingService();
        pricingService.addStragery("P001", new BogoDiscountStragery());
        pricingService.addStragery("P002", new BulkDiscountStragery(6, 0.10));

        // --- Test Cases ---
        
        System.out.println("\n--- Testing ShoppingCart ---");
        ShoppingCart cart = new ShoppingCart(pricingService, catalog);
        check("New cart should be empty", cart.getItemcount() == 0 && cart.getAllprice() == 0.0);

        cart.addItem("P001", 3); // littlePony x3
        check("Add new item correctly", cart.getItemcount() == 1 && cart.getAllprice() == 20.0); // BOGO: pay for 2

        cart.addItem("P001", 2); // littlePony x3+2=5
        check("Add existing item increases quantity", cart.getItemcount() == 1 && cart.getAllprice() == 30.0); // BOGO: pay for 3

        cart.addItem("P002", 5); // Immuna x5
        check("Add another new item", cart.getItemcount() == 2 && cart.getAllprice() == 30.0 + 25.0); // 55.0

        cart.addItem("P002", 1); // Immuna x5+1=6
        check("Add item to meet bulk discount threshold", cart.getItemcount() == 2 && cart.getAllprice() == 30.0 + (30.0 * 0.9)); // 30 + 27 = 57.0

        cart.addItem("P999", 1); // Non-existent product
        check("Adding non-existent product does not change cart", cart.getItemcount() == 2 && cart.getAllprice() == 57.0);

        cart.addItem("P003", 0); // Invalid quantity
        check("Adding item with zero quantity does not change cart", cart.getItemcount() == 2 && cart.getAllprice() == 57.0);

        cart.removeItem("P002"); // Remove Immuna
        check("Remove item correctly updates count and price", cart.getItemcount() == 1 && cart.getAllprice() == 30.0);

        cart.removeItem("P999"); // Remove non-existent item
        check("Removing non-existent item does not change cart", cart.getItemcount() == 1 && cart.getAllprice() == 30.0);
        
        cart.clearCart();
        check("Clear cart works correctly", cart.getItemcount() == 0 && cart.getAllprice() == 0.0);

        System.out.println("\n--- Testing PricingService and Strategies ---");
        CardItem singlelittlePony = new CardItem(littlePony, 1);
        CardItem twolittlePony = new CardItem(littlePony, 2);
        CardItem fiveImmuna = new CardItem(Immuna, 5);
        CardItem tenImmuna = new CardItem(Immuna, 10);
        CardItem normalLava = new CardItem(Lava, 3);

        check("BOGO Strategy (1 item)", pricingService.calculateItemPrice(singlelittlePony) == 10.0);
        check("BOGO Strategy (2 items)", pricingService.calculateItemPrice(twolittlePony) == 10.0);
        check("Bulk Strategy (below threshold)", pricingService.calculateItemPrice(fiveImmuna) == 25.0);
        check("Bulk Strategy (above threshold)", pricingService.calculateItemPrice(tenImmuna) == 45.0);
        check("Default Strategy", pricingService.calculateItemPrice(normalLava) == 60.0);

        pricingService.addStragery("P001", new BulkDiscountStragery(3, 0.50)); // เปลี่ยนโปรโมชัน littlePony เป็นซื้อ 3 ลด 50%
        CardItem threelittlePony = new CardItem(littlePony, 3);
        check("Promotion update works", pricingService.calculateItemPrice(threelittlePony) == 15.0); // 30 * 0.5 = 15.0

        System.out.println("\n--- Testing ProductCatalog ---");
        check("Find existing product", catalog.findById("P001").equals(littlePony));
        check("Find non-existent product returns null", catalog.findById("P999") == null);

        // --- สรุปผล ---
        System.out.println("\n--------------------");
        System.out.println("--- Test Summary ---");
        System.out.println("Passed: " + passedCount + ", Failed: " + failedCount);
        if (failedCount == 0) {
            System.out.println("Excellent! All tests passed!");
        } else {
            System.out.println("Some tests failed.");
        }
    }
}