import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

class loadProductHistoryTest {
    @Test
    void nullProductStream() {
        CostOfLiving testCost = new CostOfLiving();
        assertTrue( testCost.loadProductHistory( null ) < 0, "Null product history stream" );
    }

    @Test
    void emptyProductStream() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) == 0, "Empty product history stream" );
    }

    @Test
    void singleItemProductStream() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00";
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Single item in product history" );
    }

    @Test
    void endlingBlanksProductStream() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\n\n\n";
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Blank lines at end of product history" );
    }

    @Test
    void tooFewFieldsProductStream() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Too few fields" );
    }

    @Test
    void tooManyFieldsProductStream() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\t5";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Too many fields" );
    }

    @Test
    void negativeQuantity() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t-1 l\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Negative quantity" );
    }

    @Test
    void zeroQuantity() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t0 l\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Zero quantity" );
    }

    @Test
    void spacesProductName() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\t   \t1 l\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Spaces product name" );
    }

    @Test
    void emptyProductName() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\t\t1 l\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Empty product name" );
    }

    @Test
    void emptySize() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Empty size" );
    }

    @Test
    void spacesSize() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t \t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Spaces as size" );
    }

    @Test
    void sizeMissingSpace() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1l\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Size missing space separator" );
    }

    @Test
    void negativeCost() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t-1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Negative cost" );
    }

    @Test
    void zeroCost() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t0.00";
        // Zero cost is a discontinuing of the product, so it should be ok.
        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Zero cost" );
    }

    @Test
    void pennyCost() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t0.01";
        assertEquals( 1,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Penny cost cost" );
    }

    @Test
    void productTwiceSameCapitalization() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\n2024/01/02\ta\t1 l\t2.00";
        assertEquals( 2,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Product twice same capitalization" );
    }

    @Test
    void productTwiceDifferentCapitalization() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\n2024/01/02\tA\t1 l\t2.00";
        assertEquals( 2,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Product twice different capitalization" );
    }

    @Test
    void productRepeatedDifferentSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\n2024/01/01\ta\t2 l\t2.00";
        assertEquals( 2,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Product repeated with different sizes" );
    }

    @Test
    void productRepeatedVariantSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\n2024/01/02\ta\t1000 ml\t2.00";
        assertEquals( 2,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Product repeated with variants of the same size" );
    }

    @Test
    void productQuantityFloat() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1.5 l\t1.00";
        assertEquals( 1,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "Product size is a floating point value" );
    }

    @Test
    void allValidUnits() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 l\t1.00\n2024/01/01\tb\t1 ml\t1.00\n2024/01/01\tc\t1 kg\t1.00\n2024/01/01\td\t1 g\t1.00";
        assertEquals( 4,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "All valid unit sizes" );
    }

    @Test
    void invalidUnits() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 cg\t1.00";
        assertTrue( testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ) < 0,
                "Invalid unit size" );
    }

    @Test
    void integerCost() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 kg\t1";
        assertEquals( 1,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "integer cost" );
    }

    @Test
    void orderByTime() {
        CostOfLiving testCost = new CostOfLiving();
        String testData = "2024/01/01\ta\t1 kg\t1.00\n" +
                "2024/01/01\tb\t1 kg\t1.50\n"+
                "2024/01/02\ta\t1 kg\t1.75\n"+
                "2024/01/02\tb\t1 kg\t1.80\n"+
                "2024/01/03\ta\t1 kg\t1.85\n"+
        "2024/01/03\tb\t1 kg\t2.00";
        assertEquals( 6,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "chronological order" );
    }

    @Test
    void groupByProduct() {
        CostOfLiving testCost = new CostOfLiving();
        String testData =
                "2024/01/01\ta\t1 kg\t1.00\n" +
                        "2024/01/02\ta\t1 kg\t1.75\n"+
                        "2024/01/03\ta\t1 kg\t1.85\n"+
                        "2024/01/01\tb\t1 kg\t1.50\n"+
                        "2024/01/02\tb\t1 kg\t1.80\n"+
                        "2024/01/03\tb\t1 kg\t2.00"
                ;
        assertEquals( 6,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "product grouping" );
    }

    @Test
    void randomOrder() {
        CostOfLiving testCost = new CostOfLiving();
        String testData =
                "2024/01/03\tb\t1 kg\t2.00\n" +
                        "2024/01/01\tb\t1 kg\t1.50\n"+
                        "2024/01/02\ta\t1 kg\t1.75\n"+
                        "2024/01/03\ta\t1 kg\t1.85\n"+
                        "2024/01/02\tb\t1 kg\t1.80\n"+
                        "2024/01/01\ta\t1 kg\t1.00"
                ;
        assertEquals( 6,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData )) ),
                "random order" );
    }

    @Test
    void callTwice() {
        CostOfLiving testCost = new CostOfLiving();
        String testData1 = "2024/01/01\ta\t1 kg\t1.00";
        String testData2 = "2024/01/01\ta\t1 kg\t1.00\n2024/01/01\tb\t1 g\t1.00";
        assertEquals( 1,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData1 )) ),
                "first Load" );
        assertEquals( 2,  testCost.loadProductHistory( new BufferedReader( new StringReader( testData2 )) ),
                "second Load" );
    }

}