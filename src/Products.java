import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*This class represents collection of products.
It provides methods to load a list from a file and retrieve product by name.
*/
public class Products{

    //Map to store products with product name as key
    private Map<String, Product> productList;

    //Constructor to initialise the product list
    public Products(){
        this.productList = new HashMap<>();
    }

    //Method to load the product list from file
    public void loadProductList(String filename){
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            String line;

            //Reading each line from the file and splitting into parts
            while((line = br.readLine()) != null){
                String[] parts = line.split("\t");
                if(parts.length == 4){
                    //Extract the product date
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                    Date date;
                    try{
                        date = dateFormat.parse(parts[0]);
                    } catch (ParseException e) {
                        System.out.println("Invalid date format: "+parts[0]);
                        continue;
                    }
                    String name = parts[1].toLowerCase();
                    String size = parts[2];
                    float price = Float.parseFloat(parts[3].substring(1)); //remove the dollar sign

                    //Create new Product object
                    Product product = new Product(date, name, size, price);

                    //Add product to the product list
                    productList.put(name, product); //use product name as key
                } else{
                    //Print error message if the line is not in correct format
                    System.out.println("Invalid product data "+line);
                }
            }
        } catch (IOException e){
            System.out.println("Error reading product list: "+e.getMessage());
        }
    }

    //Method to retrieve a product by name
    public Product getProduct(String name){
        return productList.get(name.toLowerCase());
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