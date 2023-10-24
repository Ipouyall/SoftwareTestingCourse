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
    private Commodity commodity;

    private final int inStock = 10;
    private final float initRate = 3.0f;

    @BeforeEach
    public void setUp() {
        commodity = new Commodity();
        commodity.setId("1234");
        commodity.setName("Sample Commodity");
        commodity.setInStock(inStock);
        commodity.setInitRate(initRate);
    }

    @Test
    @DisplayName("Test updateInStock method with valid amount")
    public void testUpdateInStockValidAmount() throws NotInStock {
        int amount = -5;
        commodity.updateInStock(amount);

        assertEquals(5, commodity.getInStock());
    }

    @Test
    @DisplayName("Test updateInStock method for Invalid Amount")
    public void testUpdateInStockInvalidAmount() {
        int amount = -15;

        NotInStock exception = assertThrows(NotInStock.class, () -> commodity.updateInStock(amount));
        assertEquals("Commodity is not in stock.", exception.getMessage());
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, 4",
            "Bob, 5",
            "Charlie, 2",
            "John, 3",
    })
    @DisplayName("Test addRate method for Single User Valid Score")
    public void testAddRateSingleUser(String username, int score) throws IllegalArgumentException {
        commodity.addRate(username, score);
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
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> commodity.addRate(username, score));
        assertEquals("Invalid score, Score must be between 1 and 10", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {4, 1, 5, 10,})
    @DisplayName("Test calcRating method for Single User Single Valid Rating")
    public void testCalcRatingOneRating(int score) throws IllegalArgumentException {
        commodity.addRate("Alice", score);

        float expectedRating = (initRate + score) / 2;

        assertEquals(expectedRating, commodity.getRating(), 0.01);
    }

    @Test
    @DisplayName("Test calcRating method with one rating rewritten")
    public void testCalcRatingOneRatingReWritten() throws IllegalArgumentException {
        commodity.addRate("Alice", 4);
        commodity.addRate("Alice", 5);

        float expectedRating = (initRate + 5) / 2;

        assertEquals(expectedRating, commodity.getRating(), 0.01);
    }

    @Test
    @DisplayName("Test calcRating method with multiple ratings")
    public void testCalcRatingMultipleRatings() throws IllegalArgumentException {
        commodity.addRate("Alice", 4);
        commodity.addRate("Bob", 5);
        commodity.addRate("Charlie", 2);

        float expectedTaring = (4 + 5 + 2 + initRate) / 4;

        assertEquals(expectedTaring, commodity.getRating(), 0.001);
    }
}
