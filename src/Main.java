import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        // Create a new Products object
        Products products = new Products();
        // Load a list of products from a file
        products.loadProductList("src/productList.txt");
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
    }
}