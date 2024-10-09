import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class shoppingCartCostTest {

    @Test
    void invalidCartNumber() {
        CostOfLiving testCost = new CostOfLiving();

        assertTrue( testCost.shoppingCartCost(-1, 2024, 1 ) < 0, "Negative cart number" );
        assertTrue( testCost.shoppingCartCost(0, 2024, 1 ) < 0, "Zero cart number" );
        assertTrue( testCost.shoppingCartCost(1, 2024, 1 ) < 0, "Positive cart number but no data loaded" );

        String cartData = "a\t1 l";
        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );

        assertTrue( testCost.shoppingCartCost(cartNumber+1, 2024, 1 ) < 0, "Bad cart number while some cart exists" );

        String historyData = "2024/01/01\ta\t1 l\t1.00";
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                    "load history" );
        assertTrue( testCost.shoppingCartCost(cartNumber+1, 2024, 1 ) < 0, "Bad cart number while all other data exists fine" );

    }

    @Test
    void badDates() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData = "2024/01/01\ta\t1 l\t1.00";

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 0 ) < 0, "Month is zero" );
        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 13 ) < 0, "Month is 13" );
        assertTrue( testCost.shoppingCartCost(cartNumber, -1, 5 ) < 0, "Year is negative" );
    }

    @Test
    void edgeMonths() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData = "2023/01/01\ta\t1 l\t1.00";

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 1 ) > 0, "January month" );
        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 12 ) > 0, "December month" );
    }

    @Test
    void emptyCart() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "";
        String historyData = "2023/01/01\ta\t1 l\t1.00";

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 0f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "Empty cart" );
    }

    @Test
    void singleItemCost() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData = "2023/01/01\ta\t1 l\t1.00";

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 1f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "Single item in cart and single item in history" );
    }

    @Test
    void callOnCartWithNoHistory() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );

        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 1 ) < 0, "No product history" );
    }

    @Test
    void multiItemCart() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l\nb\t1 l";
        String historyData =
                "2023/01/01\ta\t1 l\t1.00\n"
                        + "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\tc\t1 l\t4.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 3f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "Two item in cart" );
    }

    @Test
    void quoteOnlyAfterDate() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData = "2023/01/01\ta\t1 l\t1.00";

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertTrue( testCost.shoppingCartCost(cartNumber, 2022, 1 ) < 0, "quote is after the given date" );
    }

    @Test
    void discontinuedItemNoSubstitute() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l\nb\t1 l";
        String historyData =
                "2023/01/01\ta\t1 l\t1.00\n"
                        + "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\tc\t1 l\t4.00\n"
                        + "2023/02/01\ta\t1 l\t0.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 1 ) < 0, "discontinued item no substitute" );
    }

    @Test
    void discontinuedItemSmallerSubstitute() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData =
                "2023/01/01\ta\t1 l\t1.00\n"
                        + "2023/01/01\ta\t0.5 l\t2.00\n"
                        + "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\tc\t1 l\t4.00\n"
                        + "2023/02/01\ta\t1 l\t0.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 5, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 4f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "discontinued item smaller substitute" );
    }

    @Test
    void productNotInHistory() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "z\t1 l";
        String historyData =
                "2023/01/01\ta\t1 l\t1.00\n"
                        + "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\tc\t1 l\t4.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertTrue( testCost.shoppingCartCost(cartNumber, 2024, 1 ) < 0, "item not in history" );
    }

    @Test
    void biggerSizeOnlyExists() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData =
                "2023/01/01\ta\t2 l\t1.00\n"
                        + "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\tc\t1 l\t4.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 1f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "bigger item exists" );
    }

    @Test
    void smallerSizeIsBest() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData =
                "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\ta\t400 ml\t1.00\n"
                        + "2023/01/01\tc\t1 l\t4.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 3f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "use smaller items, regular size not there" );

        // Add in the size, but more expensive
        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData + "\n2023/01/01\ta\t1 l\t10.00" )) ),
                "reload history" );

        assertEquals( 3f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "use smaller items, regular size more expensive" );

    }

    @Test
    void largerSizeIsBest() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 l";
        String historyData =
                "2023/01/01\tb\t1 l\t2.00\n"
                        + "2023/01/01\tc\t1 l\t4.00\n"
                        + "2023/01/01\ta\t1.5 l\t1.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 1f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "use larger item, regular size not there" );

        // Add in the size, but more expensive
        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData + "\n2023/01/01\ta\t1 l\t10.00" )) ),
                "reload history" );

        assertEquals( 1f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "use larger item, regular size more expensive" );

    }

    @Test
    void comboOfSizesIsBest() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1.75 l";
        String historyData =
                "2023/01/01\tb\t1 l\t8.00\n"
                        + "2023/01/01\tc\t1 l\t4.00\n"
                        + "2023/01/01\ta\t1.5 l\t2.00\n"
                        + "2023/01/01\ta\t250 ml\t1.00"
                ;

        int cartNumber = testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) );
        assertTrue( cartNumber > 0, "load cart" );
        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertEquals( 4f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "use larger item, regular size not there" );

        // Add in the size, but more expensive
        assertEquals( 5, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData + "\n2023/01/01\ta\t1 l\t10.00" )) ),
                "reload history" );

        assertEquals( 4f, testCost.shoppingCartCost(cartNumber, 2024, 1 ), "use larger item, don't consider the cheaper $3 cost of mixed sizes" );

    }

}