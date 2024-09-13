import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Product {
    public void getProducts() {
        try (BufferedReader br = new BufferedReader(new FileReader("src/productList.txt"))) {
            String str;
            while ((str = br.readLine()) != null) {

            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

