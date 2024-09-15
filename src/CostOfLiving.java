import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CostOfLiving {
    //Map to store products with product name as key
    private List<Products.Product> productList;
    private Map<Integer, Map<String, Map<String, Integer>>> carts;
    private int cartID;


    //constructor to initialise instance of the object
    public CostOfLiving() {
        productList = new ArrayList<>();
        carts = new HashMap<>();
        cartID = 1;
    }

    public List<Products.Product> getProductList() {
        return productList;
    }

    //Method to load the product list from file
    public int loadProductHistory(BufferedReader productStream){
        int count = 0;
        try{
            String line;

            //Reading each line from the file and splitting into parts
            while((line = productStream.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length == 4) {
                    //Extract the product date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date;
                    try {
                        date = dateFormat.parse(parts[0]);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format: " + parts[0]);
                        continue;
                    }
                    String name = parts[1].toLowerCase();
                    String size = parts[2].toLowerCase();
                    float price = Float.parseFloat(parts[3].substring(1)); //remove the dollar sign

                    //Create new Product object
                    Products.Product product = new Products.Product(date, name, size, price);

                    //Add product to the product list
                    productList.add(product);//use product name as key
                    count++;
                } else {
                    //Print error message if the line is not in correct format
                    System.out.println("Invalid product data " + line);
                }
            }

//            // Print the product data
//            System.out.println("Product List:");
//            for (Products.Product p : productList) {
//                System.out.println("  Name: " + p.getName());
//                System.out.println("  Date: " + p.getDate());
//                System.out.println("  Size: " + p.getSize());
//                System.out.println("  Price: $" + p.getPrice());
//                System.out.println();
//            }

            findProductByName("Instant Coffee");

        } catch (IOException e){
            System.out.println("Error reading product list: "+e.getMessage());
        }
        return count;
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

    float shoppingCartCost( int cartNumber, int year, int month ){
        return 0;
    }

    public Map<String, Float> inflation(int startYear, int startMonth, int endYear, int endMonth ) {
        return null;
    }

    public List<String> priceInversion(int year, int month, int tolerance ) {
        return null;
    }

    public void findProductByName(String name) {
        List<Products.Product> productsByName = new ArrayList<>();
        for (Products.Product p : productList) {
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                productsByName.add(p);
            }
        }

        // Print the product data
//        if (!productsByName.isEmpty()) {
//            System.out.println("Products found:");
//            for (Products.Product p : productsByName) {
//                System.out.println("  Name: " + p.getName());
//                System.out.println("  Date: " + p.getDate());
//                System.out.println("  Size: " + p.getSize());
//                System.out.println("  Price: $" + p.getPrice());
//                System.out.println();
//            }
//        } else {
//            System.out.println("No products found");
//        }
    }
}
