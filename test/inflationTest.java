import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class inflationTest {

    @Test
    void noHistory() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "";

        assertEquals( 0, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2023, 12, 2024, 1 );
        assertNotNull( testResult , "No history" );
        assertEquals( 0, testResult.size(), "and what is returned should be empty" );

    }

    @Test
    void badDates() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertNull( testCost.inflation(2024, 0, 2024, 4 ), "Start month is zero" );
        assertNull( testCost.inflation(2024, 1, 2025, 0 ), "End month is zero" );
        assertNull( testCost.inflation(2024, 13, 2025, 4 ), "Start month is 13" );
        assertNull( testCost.inflation(2024, 1, 2025, 13 ), "End month is 13" );

        // Won't check for negative start or end years since I didn't give those in the test cases
    }

    @Test
    void goodDatesButBadOrder() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertNull( testCost.inflation(2025, 1, 2024, 4 ), "Start year after end year" );
        assertNull( testCost.inflation(2024, 4, 2024, 1 ), "Same year but start month after end month" );
    }

    @Test
    void sameStartAndEnd() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertNotNull( testCost.inflation(2024, 1, 2024, 1 ), "Same start and end dates" );
    }

    @Test
    void oneHistoryItemOneRelevantPrice() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 2 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 0, testResult.size(), "and what is returned should be empty" );
    }

    @Test
    void oneHistoryItemTwoRelevantPrices() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 1, testResult.size(), "and what is returned should be empty" );
        Set<String> theKeys = testResult.keySet();
        assertTrue( theKeys.contains( "a 1 l" ), "the single product should be right" );
        assertEquals( 1f, testResult.get( "a 1 l" ), "inflation value should be correct" );
    }

    @Test
    void callInflationTwice() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult1 = testCost.inflation(2024, 1, 2024, 4 );
        Map<String, Float> testResult2 = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult1, "Something should be returned" );
        assertEquals( 1, testResult1.size(), "and what is returned should be not empty" );
        Set<String> theKeys = testResult1.keySet();
        assertTrue( theKeys.contains( "a 1 l" ), "the single product should be right" );
        assertEquals( 1f, testResult1.get( "a 1 l" ), "inflation value should be correct" );

        assertNotNull( testResult2, "Something should be returned" );
        assertEquals( 1, testResult2.size(), "and what is returned should be not empty" );
        theKeys = testResult2.keySet();
        assertTrue( theKeys.contains( "a 1 l" ), "the single product should be right" );
        assertEquals( 1f, testResult2.get( "a 1 l" ), "inflation value should be correct" );
    }

    @Test
    void multipleItemsOneWithOnePrice() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/03/06\tb\t1 l\t2.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 1, testResult.size(), "and what is returned should not be empty" );
        Set<String> theKeys = testResult.keySet();
        assertTrue( theKeys.contains( "a 1 l" ), "the single product should be right" );
        assertEquals( 1f, testResult.get( "a 1 l" ), "inflation value should be correct" );
    }

    @Test
    void multipleItemsOneWithManyPrices() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2023/01/06\tb\t1 l\t2.00\n"
                + "2024/02/06\tb\t1 l\t2.50\n"
                + "2024/03/06\tb\t1 l\t3.00\n"
                + "2024/03/06\ta\t1 l\t1.00";

        assertEquals( 5, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 1, testResult.size(), "and what is returned should not be empty" );
        Set<String> theKeys = testResult.keySet();
        assertTrue( theKeys.contains( "b 1 l" ), "the single product should be right" );
        // Use a range here to account for possible floating point error, given that the division isn't as clean as it could be in the test data
        assertTrue( (testResult.get( "b 1 l" ) >= 0.49f) && (testResult.get("b 1 l") <= 0.5f), "inflation value should be correct" );
    }

    @Test
    void multipleItemsNothingInflated() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/06\tb\t1 l\t2.00\n"
                + "2024/02/06\tb\t1 l\t2.00\n"
                + "2024/03/06\tb\t1 l\t2.00\n"
                + "2024/03/06\ta\t1 l\t1.00";

        assertEquals( 5, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 0, testResult.size(), "and what is returned should be empty" );
    }

    @Test
    void shrinkflationHappened() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t4.00\n"
                + "2024/02/01\ta\t1 l\t0.00\n"
                + "2024/02/06\ta\t0.75 l\t6.00";

        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 1, testResult.size(), "and what is returned should not be empty" );
        Set<String> theKeys = testResult.keySet();
        assertTrue( theKeys.contains( "a 1 l" ), "the single product should be right" );
        assertEquals( 1f, testResult.get( "a 1 l" ), "inflation value should be correct" );
    }

    @Test
    void discontinuedDelayedIntroduction() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t4.00\n"
                + "2024/02/01\ta\t1 l\t0.00\n"
                + "2024/03/06\ta\t0.75 l\t4.00";

        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 0, testResult.size(), "and what is returned should be empty" );
    }

    @Test
    void differingUnitSpecs() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1000 ml\t1.00\n"
                + "2024/01/01\tb\t1 kg\t2.00\n"
                + "2024/03/06\tb\t1000 g\t8.00\n"
                + "2024/03/06\ta\t1 l\t2.00";

        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 2, testResult.size(), "and what is returned should not be empty" );
        Set<String> theKeys = testResult.keySet();
        assertTrue( theKeys.contains( "a 1 l" ), "product name a right" );
        assertTrue( theKeys.contains( "b 1000 g" ), "product name b right" );
        assertEquals( 1f, testResult.get( "a 1 l" ), "inflation value for a" );
        assertEquals( 3f, testResult.get( "b 1000 g" ), "inflation value for b" );
    }

    @Test
    void itemGetsCheaper() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t2.00\n"
                + "2024/03/06\ta\t1 l\t1.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 0, testResult.size(), "and what is returned should be empty" );
    }

    @Test
    void shrinkflationHappenedFromBiggerItem() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t4.00\n"
                + "2024/02/01\ta\t1 l\t0.00\n"
                + "2024/02/06\ta\t1.5 l\t9.00";

        assertEquals( 3, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        // Reading carefully, the instructions say that shrinkflation just happens with a smaller product, not a bigger one.
        assertEquals( 0, testResult.size(), "Shrinkflation define as coming from a smaller alternative" );
        //assertEquals( 1, testResult.size(), "and what is returned should not be empty" );
        //Set<String> theKeys = testResult.keySet();
        //assertTrue( theKeys.contains( "a 1 l" ), "the single product should be right" );
        //assertEquals( 1.5f, testResult.get( "a 1 l" ), "inflation value should be correct" );
    }

    @Test
    void productNameCaseChange() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\tab\t1 l\t4.00\n"
                + "2024/02/01\taB\t1 l\t5.00";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 1, testResult.size(), "and what is returned should not be empty" );
        Set<String> theKeys = testResult.keySet();
        for(String key : theKeys) {
            assertEquals( "ab 1 l", key.toLowerCase(), "the single product should be right" );
        }
    }

    @Test
    void multipleSizesOneProduct() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1000 ml\t2.00\n"
                + "2024/01/01\ta\t500 ml\t1.50\n"
                + "2024/03/06\ta\t1000 ml\t4.00\n"
                + "2024/03/06\ta\t500 ml\t4.50";

        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        Map<String, Float> testResult = testCost.inflation(2024, 1, 2024, 4 );
        assertNotNull( testResult, "Something should be returned" );
        assertEquals( 2, testResult.size(), "and what is returned should not be empty" );
        Set<String> theKeys = testResult.keySet();
        assertTrue( theKeys.contains( "a 1000 ml" ), "product name a right" );
        assertTrue( theKeys.contains( "a 500 ml" ), "product name b right" );
        assertEquals( 1f, testResult.get( "a 1000 ml" ), "inflation value for 1 litre" );
        // Use a range here to account for possible floating point error, given that the division isn't as clean as it could be in the test data

        assertTrue( (testResult.get( "a 500 ml" )  >= 1.9f) && (testResult.get("a 500 ml") <= 2f), "inflation value for 0.5 litre" );
    }

}