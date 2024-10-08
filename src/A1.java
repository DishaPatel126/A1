import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class A1 {
    public static void main(String[] args) {

        CostOfLiving costOfLiving = new CostOfLiving();
        try(BufferedReader productStream = new BufferedReader(new FileReader("src/productList.txt"))){
            int count = costOfLiving.loadProductHistory(productStream);
            System.out.println("Loaded "+ count +" products entries.");
        } catch (IOException e) {
            System.out.println("Error in reading product history: " +e.getMessage());
        }

        try(BufferedReader cartStream = new BufferedReader(new FileReader("src/shoppingCart.txt"))){
            int cartID = costOfLiving.loadShoppingCart(cartStream);
            if(cartID > 0){
                System.out.println("Loaded shopping cart with ID: "+cartID);
                System.out.printf("Shopping cart cost: %.2f%n", costOfLiving.shoppingCartCost(cartID, 2024, 10));
            } else {
                System.out.println("Error loading shopping cart");
            }
        } catch (IOException e) {
            System.out.println("Error in shopping cart file: " +e.getMessage());
        }

        Map<String, Float> inflationReport = costOfLiving.inflation(2024, 1, 2024, 9);
        if (inflationReport != null) {
            System.out.println("Inflation Report");
            for (Map.Entry<String, Float> entry : inflationReport.entrySet()) {
                System.out.println("Product: " + entry.getKey() + ", Inflation: " + (entry.getValue()) + "%");
            }
        } else {
            System.out.println("No inflation data available.");
        }
        List<String> priceInversionReport = costOfLiving.priceInversion(2024, 9, 5);
        if (priceInversionReport != null) {
            System.out.println("Price Inversions:");
            for (String inversion : priceInversionReport) {
                System.out.println(inversion);
            }
        } else {
            System.out.println("No price inversions found.");
        }
    }
}