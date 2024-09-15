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

        CostOfLiving costOfLiving = new CostOfLiving();
//        CostOfLiving products = new CostOfLiving();
        try(BufferedReader productStream = new BufferedReader(new FileReader("src/productList.txt"))){
            int count = costOfLiving.loadProductHistory(productStream);
            System.out.println("Loaded "+ count +" products entries.");
        } catch (IOException e) {
            System.out.println("Error in reading product history: " +e.getMessage());
        }

//        CostOfLiving shoppingCarts = new CostOfLiving();
        try(BufferedReader cartStream = new BufferedReader(new FileReader("src/shoppingCart.txt"))){
            int cartID = costOfLiving.loadShoppingCart(cartStream);
            if(cartID > 0){
                System.out.println("Loaded shopping cart with ID: "+cartID);
                System.out.println("Shopping cart cost: "+costOfLiving.shoppingCartCost(cartID, 2024, 01));

            } else {
                System.out.println("Error loading shopping cart");
            }
        } catch (IOException e) {
            System.out.println("Error in shopping cart file: " +e.getMessage());
        }


    }
}