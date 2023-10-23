package model;

import exceptions.NotInStock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

public class CommodityTest {
    private Commodity commodity;

    private int inStock = 10;
    private float initRate = 3.0f;

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
    @DisplayName("Test updateInStock method with invalid amount")
    public void testUpdateInStockInvalidAmount() {
        int amount = -15;
        assertThrows(NotInStock.class, () -> commodity.updateInStock(amount));
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, 4",
            "Bob, 5",
            "Charlie, 2",
            "John, 3",
    })
    @DisplayName("Test addRate method with single user")
    public void testAddRateSingleUser(String username, int score) {
        commodity.addRate(username, score);
        float expectedRating = (initRate + score) / 2;
        assertEquals(expectedRating, commodity.getRating(), 0.01);
    }

    @Test
    @DisplayName("Test calcRating method with one rating")
    public void testCalcRatingOneRating() {
        commodity.addRate("Alice", 4);
        assertEquals(3.5, commodity.getRating(), 0.01);
    }

    @Test
    @DisplayName("Test calcRating method with one rating rewritten")
    public void testCalcRatingOneRatingReWritten() {
        commodity.addRate("Alice", 4);
        commodity.addRate("Alice", 5);
        assertEquals(4, commodity.getRating(), 0.01);
    }

    @Test
    @DisplayName("Test calcRating method with multiple ratings")
    public void testCalcRatingMultipleRatings() {
        commodity.addRate("Alice", 4);
        commodity.addRate("Bob", 5);
        commodity.addRate("Charlie", 2);
        assertEquals(3.5, commodity.getRating(), 0.001);
    }
}
