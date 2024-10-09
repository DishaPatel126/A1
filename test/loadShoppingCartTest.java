import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class loadShoppingCartTest {

    @Test
    void nullCartStream() {
        CostOfLiving testCost = new CostOfLiving();
        assertTrue( testCost.loadShoppingCart( null ) < 0, "Null shopping cart stream" );
    }

    @Test
    void emptyCartStream() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "";
        // I'll allow for an empty cart.  It's not an interesting cart, but there's nothing _invalid_ about it
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) > 0, "Empty cart stream" );
    }

    @Test
    void oneItemInCart() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) > 0,
                "single item in cart" );
    }

    @Test
    void endingBlankLines() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\n\n";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) > 0,
                "blank lines at end of cart" );
    }

    @Test
    void tooFewFields() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a 1 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "line with too few fields" );
    }

    @Test
    void tooManyFields() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\t1.00";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "line with too many fields" );
    }

    @Test
    void negativeQuantity() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t-1 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "line with negative quantity" );
    }

    @Test
    void zeroQuantity() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t0 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "line with zero quantity" );
    }

    @Test
    void emptyProductName() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "\t1 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "empty product name" );
    }

    @Test
    void spacesProductName() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = " \t1 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "spaces product name" );
    }

    @Test
    void emptySize() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "empty size" );
    }

    @Test
    void spacesAsSize() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t ";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "spaces as size" );
    }

    @Test
    void noSpaceInSize() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "size not delineated properly" );
    }

    @Test
    void uniqueItems() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\nb\t3 l\nc\t2 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) > 0,
                "unique set of items" );
    }

    @Test
    void duplicateItemSameSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\na\t1 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "duplicate items" );
    }

    @Test
    void duplicateItemSameSizesButVariants() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\na\t1000 ml";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "duplicate items variant sizes" );
    }

    @Test
    void duplicateItemDifferentSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\na\t2 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) > 0,
                "duplicate items different sizes" );
    }

    @Test
    void quantityIsFloat() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1.5 l";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) >  0,
                "quantity is a float" );
    }

    @Test
    void allValidUnitSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l\nb\t1 ml\nc\t1 kg\nd\t1 g";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) > 0,
                "all valid unit sizes" );
    }

    @Test
    void validUnits() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 dl";
        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) ) < 0,
                "invalid units" );
    }

    @Test
    void capitalizedUnits() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 mL";

        // specification is silent on using capitalization in the units, so just make sure that we don't crash.

        testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) );
    }


    @Test
    void loadAfterHistory() {
        CostOfLiving testCost = new CostOfLiving();
        String cartData = "a\t1 ml";
        String historyData = "2024/01/01\ta\t1 kg\t1.00";
        assertEquals( 1,  testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history data" );

        // specification is silent on using capitalization in the units, so just make sure that we don't crash.

        assertTrue( testCost.loadShoppingCart( new BufferedReader( new StringReader( cartData )) ) > 0,
                "load cart data" );
    }

    @Test
    void sameCartTwice() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "a\t1 l";
        int cart1 = testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) );
        int cart2 = testCost.loadShoppingCart( new BufferedReader( new StringReader( testData )) );

        assertTrue( cart1 > 0, "first load" );
        assertTrue( cart2 > 0, "second load" );
        assertTrue( cart1 !=- cart2, "check that carts are different" );
    }

    @Test
    void loadTwoCarts() {
        CostOfLiving testCost = new CostOfLiving();
        String testData1 = "a\t1 l";
        String testData2 = "b\t1 ml";
        int cart1 = testCost.loadShoppingCart( new BufferedReader( new StringReader( testData1 )) );
        int cart2 = testCost.loadShoppingCart( new BufferedReader( new StringReader( testData2 )) );

        assertTrue( cart1 > 0, "first load" );
        assertTrue( cart2 > 0, "second load" );
        assertTrue( cart1 !=- cart2, "check that carts are different" );
    }

}