import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
* This class represents collection of shopping carts.
* It provides methods to load a cart form file and return the cartID.
*/
public class ShoppingCarts {

    //Map to store product details and quantity with cartID as its key.
    private Map<Integer, Map<String, Map<String, Integer>>> carts;
    private int cartID;

    //Constructor to initialise the shoppingCart
    public ShoppingCarts() {
        carts = new HashMap<>();
        cartID = 1;
    }

    //Method to load the shopping cart list from file
    public int loadShoppingCart(BufferedReader cartStream) {
        try {
            String line;
            //Mapping the size and quantity with the product name
            Map<String, Map<String, Integer>> cart = new HashMap<>();

            //Reading each line from the file and splitting into parts
            while ((line = cartStream.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 3) {
                    System.out.println("Invalid line format: " + line);
                    return -1;
                }

                String productName = parts[0].trim().toLowerCase();
                String size = parts[1].trim().toLowerCase();
                try {
                    int quantity = Integer.parseInt(parts[2].trim());

                    //Quantity check
                    if (quantity <= 0) {
                        System.out.println("Invalid quantity");
                        return -1;
                    }

                    //Check if the product name already exists or not.
                    if (!cart.containsKey(productName)) {
                        cart.put(productName, new HashMap<>());
                    }
                    //Retrieves inner map to store different data with same product name.
                    cart.get(productName).put(size, quantity);
                } catch (NumberFormatException e) {
                    System.out.println("Invalid quantity: " + parts[2]);
                    return -1;
                }
            }

            if (cart.isEmpty()) {
                System.out.println("Shopping cart is empty");
                return -1;
            }

            //Add the cart to the map
            carts.put(cartID, cart);
            return cartID++;
        } catch (IOException e) {
            System.out.println("Error in reading product history: " + e.getMessage());
            return -1;
        }
    }

    public float shoppingCartCost(int cartNumber, int year, int month) {
        // Get the cart from the map
        Map<String, Map<String, Integer>> cart = carts.get(cartNumber);
        System.out.println("Cart: "+cart);
        if (cart == null) {
            return -2.0f; // Cart not found
        }
        float totalCost = 0.0f;

        // Iterate over each product in the cart
        for (Map.Entry<String, Map<String, Integer>> entry : cart.entrySet()) {
            String productName = entry.getKey();
            Map<String, Integer> productSizes = entry.getValue();

            System.out.println(productName);
            System.out.println(productSizes);

            // Find the best packaging for this product
            Packaging bestPackaging = findBestPackaging(productName, productSizes, year, month);

            if (bestPackaging == null) {
                return -1.0f; // No packaging found for this product
            }

            // Calculate the cost of this product
            float productCost = bestPackaging.getCost() * bestPackaging.getQuantity();
            totalCost += productCost;
        }

        return totalCost;
    }

    // Helper method to find the best packaging for a product
    private Packaging findBestPackaging(String productName, Map<String, Integer> productSizes, int year, int month) {
        // Load the product list from file (assuming it's in a file called "products.txt")
        Map<String, List<Packaging>> productList = loadProductList("src/productList.txt");

        // Get the packaging options for this product
        List<Packaging> packagingOptions = productList.get(productName);
        if (packagingOptions == null) {
            return null; // Product not found
        }

        // Initialize the best packaging to null
        Packaging bestPackaging = null;

        // Iterate over each packaging option
        for (Packaging packaging : packagingOptions) {
            // Check if this packaging option can meet the required quantity
            if (canMeetQuantity(packaging, productSizes)) {
                // Calculate the cost of this packaging option
                float cost = packaging.getCost() * packaging.getQuantity();

                // If this is the best option so far, update the best packaging
                if (bestPackaging == null || cost < bestPackaging.getCost() * bestPackaging.getQuantity()) {
                    bestPackaging = packaging;
                }
            }
        }

        return bestPackaging;
    }

    // Helper method to check if a packaging option can meet the required quantity
    private boolean canMeetQuantity(Packaging packaging, Map<String, Integer> productSizes) {
        // Calculate the total quantity of this packaging option
        int totalQuantity = 0;
        for (Map.Entry<String, Integer> entry : productSizes.entrySet()) {
            totalQuantity += entry.getValue() * packaging.getSize();
        }

        // Check if the total quantity meets the required quantity
        return totalQuantity >= productSizes.values().stream().mapToInt(Integer::intValue).sum();
    }

    // Helper method to load the product list from file
    private Map<String, List<Packaging>> loadProductList(String filename) {
        // Implement this method to load the product list from file
        // For now, return an empty map
        return new HashMap<>();
    }

    // Helper class to represent a packaging option
    private static class Packaging {
        private String productName;
        private int size;
        private float cost;
        private int quantity;

        public Packaging(String productName, int size, float cost, int quantity) {
            this.productName = productName;
            this.size = size;
            this.cost = cost;
            this.quantity = quantity;
        }

        public int getSize() {
            return size;
        }

        public float getCost() {
            return cost;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}
