import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/*
* This class represents collection of products.
*/
public class Products{

    CostOfLiving products = new CostOfLiving();
    List<Product> productList = products.getProductList();
    //Constructor to initialise the product list
    public Products(){
    }

    //Method to retrieve a product by name
    public List<Product> getProductsByName(String name) {
        // Create a list to store the products
        List<Product> products = new ArrayList<>();

        // Iterate over the products in the list
        for (Product product : productList) {
            // Check if the product name matches
            if (product.getName().equalsIgnoreCase(name)) {
                // Add the product to the list if found
                products.add(product);
            }
        }
        // Return the list of products
        return products;
    }

    //Inner class that represents a product
    public static class Product{
        private Date date;
        private String name;
        private String size;
        private float price;

        //Constructor to initialise the product data
        public Product(Date date, String name, String size, float price){
            this.date = date;
            this.name = name;
            this.size = size;
            this.price = price;
        }

        public Date getDate() {
            return date;
        }

        public String getName() {
            return name;
        }

        public String getSize() {
            return size;
        }

        public float getPrice() {
            return price;
        }
    }
}