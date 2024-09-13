import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

public class CostOfLiving {

    //constructor to initialise instance of the object
    public void CostOfLiving() {
    }

    public int loadProductHistory( BufferedReader productStream ) {
        return -1;
    }

    public int loadShoppingCard( BufferedReader cartStream ) {
        return -1;
    }

    public float shoppingCardCost( int cartNumber, int year, int month ) {
        return -1.0f;
    }

    public Map<String, Float> inflation(int startYear, int startMonth, int endYear, int endMonth ) {
        return null;
    }

    public List<String> priceInversion(int year, int month, int tolerance ) {
        return null;
    }
}
