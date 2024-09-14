import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Products products = new Products();
        try(BufferedReader productStream = new BufferedReader(new FileReader("src/productList.txt"))){
            int count = products.loadProductHistory(productStream);
            System.out.println("Loaded "+ count +" products entries.");
        } catch (IOException e) {
            System.out.println("Error in reading product history: " +e.getMessage());
        }

        ShoppingCarts shoppingCarts = new ShoppingCarts();
        try(BufferedReader cartStream = new BufferedReader(new FileReader("src/shoppingCart.txt"))){
            int cartID = shoppingCarts.loadShoppingCart(cartStream);
            if(cartID > 0){
                System.out.println("Loaded shopping cart with ID: "+cartID);

                //Calculate the cost of shopping cart
//                ShoppingCartCalculator calculator = new ShoppingCartCalculator();
                float totalCost = shoppingCarts.shoppingCartCost(cartID, 2024, 02);

                System.out.println("Total cost of cart: "+totalCost);
            } else {
                System.out.println("Error loading shopping cart");
            }
        } catch (IOException e) {
            System.out.println("Error in shopping cart file: " +e.getMessage());
        }

        /*
        // Create a new Products object
        Products products = new Products();
        // Load a list of products from a file
        products.loadProductList();
        // Retrieve a product by name
        Products.Product product = products.getProduct("Apple Juice");
        if (product != null) {
            // Print the product data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            System.out.println(dateFormat.format(product.getDate()) + "\t" + product.getName() + "\t" + product.getSize() + "\t$" + product.getPrice());
        } else {
            // Print an error message if the product is not found
            System.out.println("Product not found");
        }
        */


    }
}