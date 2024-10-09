import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class priceInversionTest {
    @Test
    void badDates() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00";

        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertNull( testCost.priceInversion(-1, 6, 10 ), "year is negative" );
        assertNull( testCost.priceInversion(2024, 0, 10 ), "month is zero" );
        assertNull( testCost.priceInversion(2024, 13, 10 ), "month is 13" );
    }

    @Test
    void badTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00";

        assertEquals( 1, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        assertNull( testCost.priceInversion(2024, 6, -1 ), "tolerance is negative" );
        // Tolerance of 101% has undefined behaviour.  Just make sure we don't crash.
        testCost.priceInversion(2024, 6, 101 );
    }

    @Test
    void toleranceLimits() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.50";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        // Designed with no inversions, so expect an empty list back.
        List<String> zeroTolerance = testCost.priceInversion(2024, 6, 0 );
        List<String> fullTolerance = testCost.priceInversion(2024, 6, 100 );
        assertNotNull( zeroTolerance, "tolerance is zero" );
        assertEquals( 0, zeroTolerance.size(), "zero tolerance size");
        assertNotNull( fullTolerance, "tolerance is 100%" );
        assertEquals( 0, fullTolerance.size(), "full tolerance size");
    }

    @Test
    void edgeMonths() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.50";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        // Designed with no inversions, so expect an empty list back.
        List<String> smallMonth = testCost.priceInversion(2024, 1, 10 );
        List<String> bigMonth = testCost.priceInversion(2024, 12, 10 );
        assertNotNull( smallMonth, "January" );
        assertEquals( 0, smallMonth.size(), "January size");
        assertNotNull( bigMonth, "December" );
        assertEquals( 0, bigMonth.size(), "December size");
    }

    @Test
    void emptyHistory() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "";

        assertEquals( 0, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        // Designed with no inversions, so expect an empty list back.
        List<String> outcome = testCost.priceInversion(2024, 1, 10 );
        assertNotNull( outcome, "no history still has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void beforeHistoryStarted() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.50";

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2023, 12, 10 );
        assertNotNull( outcome, "before history starts still has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void twoProductSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.75\n"
                + "2024/01/01\tb\t1 l\t2.00\n"
                + "2024/01/01\tb\t0.5 l\t0.50\n"
                ;

        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 1, outcome.size(), "size check");
        assertEquals( "b\t1 l\t0.5 l", outcome.get(0), "");
    }

    @Test
    void moreThanTwoProductSizes() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.10\n"
                + "2024/01/01\ta\t0.25 l\t0.10\n"
                + "2024/01/01\tb\t0.1 l\t0.50"
                ;

        // Sizes set so that the 500 ml is the most economical then the 250ml.
        assertEquals( 4, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 2, outcome.size(), "size check");
        assertTrue( outcome.contains("a\t1 l\t0.5 l"), "1l vs 500ml" );
        assertTrue( outcome.contains("a\t1 l\t0.25 l"), "1l vs 250ml" );
    }

    @Test
    void largerCheaperBeyondTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t1.50"
                ;

        // Sizes set so that the 500 ml is the most economical then the 250ml.
        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void largerCheaperAtTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1000.00\n"
                + "2024/01/01\ta\t0.5 l\t550.0"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void largerCheaperBelowTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.53"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void samePerUnitCost() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.50"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void largerMoreExpensiveAboveTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.40"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 1, outcome.size(), "size check");
        assertTrue( outcome.contains("a\t1 l\t0.5 l"), "inversion beyond tolerance" );
    }

    @Test
    void largerMoreExpensiveAtTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.1 l\t.9"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void largerMoreExpensiveBelowTolerance() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.47"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 0, outcome.size(), "size check");
    }

    @Test
    void reportSeveralProductInversions() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.40\n"
                + "2024/01/01\tb\t2 kg\t1.00\n"
                + "2024/01/02\tb\t2000 g\t10.00\n"
                + "2024/01/01\tb\t1 kg\t4.00"
                ;

        assertEquals( 5, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome, "has a list" );
        assertEquals( 2, outcome.size(), "size check");
        assertTrue( outcome.contains("a\t1 l\t0.5 l"), "product A" );
        assertTrue( outcome.contains("b\t2000 g\t1 kg"), "product B" );
    }

    @Test
    void callTwiceInARow() {
        CostOfLiving testCost = new CostOfLiving();
        String historyData = "2024/01/01\ta\t1 l\t1.00\n"
                + "2024/01/01\ta\t0.5 l\t0.40"
                ;

        assertEquals( 2, testCost.loadProductHistory( new BufferedReader( new StringReader( historyData )) ),
                "load history" );

        List<String> outcome1 = testCost.priceInversion(2024, 2, 10 );
        List<String> outcome2 = testCost.priceInversion(2024, 2, 10 );
        assertNotNull( outcome1, "first call outcome" );
        assertNotNull( outcome2, "second call outcome" );
        assertEquals( 1, outcome1.size(), "first size check");
        assertEquals( 1, outcome2.size(), "second size check");
        assertTrue( outcome1.contains("a\t1 l\t0.5 l"), "first content" );
        assertTrue( outcome2.contains("a\t1 l\t0.5 l"), "second content" );
    }

}