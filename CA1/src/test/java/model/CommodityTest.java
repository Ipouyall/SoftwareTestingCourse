package model;

import exceptions.NotInStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {

    public static Commodity createAnonymousCommodity() {
        Commodity commodity = new Commodity();
        commodity.setId("123");

        return commodity;
    }

    public static Commodity createAnonymousCommodityWithInStock(int inStock) {
        Commodity commodity = createAnonymousCommodity();
        commodity.setInStock(inStock);

        return commodity;
    }

    public static Commodity createAnonymousCommodityWithInitRate(int initRate) {
        Commodity commodity = createAnonymousCommodity();
        commodity.setInitRate(initRate);

        return commodity;
    }

    @Test
    @DisplayName("Test updateInStock method with valid amount")
    public void testUpdateInStockValidAmount() throws NotInStock {
        // SetUp
        Commodity commodity = createAnonymousCommodityWithInStock(10);

        // Execute
        int amount = -5;
        commodity.updateInStock(amount);

        // Validate
        assertEquals(5, commodity.getInStock(), 0.001);

        // Teardown -> Garbage Collector
    }

    @Test
    @DisplayName("Test updateInStock method for Invalid Amount")
    public void testUpdateInStockInvalidAmount() {
        // SetUp
        Commodity commodity = createAnonymousCommodityWithInStock(10);

        // Execute & Validate
        NotInStock exception = assertThrows(NotInStock.class, () -> commodity.updateInStock(-15));
        assertEquals("Commodity is not in stock.", exception.getMessage());

        // Teardown -> Garbage Collector
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, 4, 1, 2.5",
            "Alice, 1, 3, 2",
            "Alice, 5, 5, 5",
    })
    @DisplayName("Test calcRating method for Single User Single Valid Rating Specified initRate")
    public void testCalcRatingOneRating(String uname, int score, int initRate, float expectedRate) throws IllegalArgumentException {
        // SetUp
        Commodity commodity = createAnonymousCommodityWithInitRate(initRate);

        // Execute
        commodity.addRate(uname, score);

        // Validate
        assertEquals(expectedRate, commodity.getRating(), 0.001);

        // Teardown -> Garbage Collector
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, -4",
            "Bob, 15",
            "Charlie, 0",
            "John, 11",
    })
    @DisplayName("Test addRate method for Single User Invalid Score out of range")
    public void testAddRateSingleOutRange(String username, int score) {
        // SetUp
        Commodity commodity = createAnonymousCommodity();

        // Execute & Validate
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commodity.addRate(username, score));
        assertEquals("Invalid score, Score must be between 1 and 10", exception.getMessage());

        // Teardown -> Garbage Collector
    }

    @Test
    @DisplayName("Test calcRating method with one rating rewritten with initRate")
    public void testCalcRatingOneRatingReWrittenWithInitRate() throws IllegalArgumentException {
        // SetUp
        Commodity commodity = createAnonymousCommodityWithInitRate(3);

        // Execute
        commodity.addRate("Alice", 4);
        commodity.addRate("Alice", 5);

        // Validate
        assertEquals(4, commodity.getRating(), 0.001);

        // Teardown -> Garbage Collector
    }

    @Test
    @DisplayName("Test calcRating method with multiple ratings with initRate")
    public void testCalcRatingMultipleRatings() throws IllegalArgumentException {
        // SetUp
        Commodity commodity = createAnonymousCommodityWithInitRate(3);

        commodity.addRate("Alice", 4);
        commodity.addRate("Bob", 5);
        commodity.addRate("Charlie", 2);

        assertEquals(3.5f, commodity.getRating(), 0.001);

        // Teardown -> Garbage Collector
    }
}
