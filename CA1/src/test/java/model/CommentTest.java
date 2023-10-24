package model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    private Comment comment;
    private final int id = 1;
    private final String email = "user@example.com";
    private final String name = "John";;
    private final int cId = 123;
    private final String content = "Sample comment";

    @BeforeEach
    public void setUpTest() {
        comment = new Comment(id, email, name, cId, content);
    }

    @Test
    public void testId() {
        int actualId = comment.getId();

        assertEquals(id, actualId);
    }

    @Test
    public void testEmail() {
        String actualEmail = comment.getUserEmail();

        assertEquals(email, actualEmail);
    }

    @Test
    public void testName() {
        String actualName = comment.getUsername();

        assertEquals(name, actualName);
    }

    @Test
    public void testCommodityId() {
        int actualCId = comment.getCommodityId();

        assertEquals(cId, actualCId);
    }

    @Test
    public void testTextContent() {
        String actualContent = comment.getText();

        assertEquals(content, actualContent);
    }

    @Test
    public void testDateNotNull() {
        String currentDate = comment.getDate();

        assertNotNull(currentDate);
    }

    @Test
    public void testDateFormat() throws ParseException {
        String currentDate = comment.getDate();

        String dateFormatRegex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        assertTrue(currentDate.matches(dateFormatRegex));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parsedDate = dateFormat.parse(currentDate);
        assertNotNull(parsedDate);
    }

    @ParameterizedTest
    @CsvSource({
            "like, 1",
            "dislike, 0",
    })
    @DisplayName("Test addUserVote method for Single Valid Vote from Single User for like count")
    public void testAddUserVoteLike(String vote, int expectedLikes) throws IllegalArgumentException {
        // Execute
        comment.addUserVote("Alice", vote);
        int actualLikes = comment.getLike();

        // Validate
        assertEquals(expectedLikes, actualLikes);
    }

    @ParameterizedTest
    @CsvSource({
            "like, 0",
            "dislike, 1",
    })
    @DisplayName("Test addUserVote method for Single Valid Vote from Single User for dislike count")
    public void testAddUserVoteDislike(String vote, int expectedDislikes) throws IllegalArgumentException {
        // Execute
        comment.addUserVote("Alice", vote);
        int actualDislikes = comment.getDislike();

        // Validate
        assertEquals(expectedDislikes, actualDislikes);
    }

    @ParameterizedTest
    @CsvSource({
            "Ali Adam Alice, like like like, 3",
            "Ali Adam Alice, dislike dislike dislike, 0",
            "Ali Adam Alice, like dislike like, 2",
            "Ali Adam Alice, dislike like dislike, 1",
    })
    @DisplayName("Test addUserVote method for Multiple Valid Votes from Multiple Different Users for like count")
    public void testAddUserVotesDifferentUserLike(String names, String votes, int expectedLikes) throws IllegalArgumentException {
        // Convert names and votes to lists
        List<String> nameList = Arrays.asList(names.split("\\s+"));
        List<String> voteList = Arrays.asList(votes.split("\\s+"));

        // Execute
        for (int i = 0; i < nameList.size(); i++) {
            comment.addUserVote(nameList.get(i), voteList.get(i));
        }

        int actualLikes = comment.getLike();

        // Validate
        assertEquals(expectedLikes, actualLikes);
    }

    @ParameterizedTest
    @CsvSource({
            "Ali Adam Alice, like like like, 0",
            "Ali Adam Alice, dislike dislike dislike, 3",
            "Ali Adam Alice, like dislike like, 1",
            "Ali Adam Alice, dislike like dislike, 2",
    })
    @DisplayName("Test addUserVote method for Multiple Valid Votes from Multiple Different Users for dislike count")
    public void testAddUserVotesDifferentUserDislike(String names, String votes, int expectedDislikes) throws IllegalArgumentException {
        // Convert names and votes to lists
        List<String> nameList = Arrays.asList(names.split("\\s+"));
        List<String> voteList = Arrays.asList(votes.split("\\s+"));

        // Execute
        for (int i = 0; i < nameList.size(); i++) {
            comment.addUserVote(nameList.get(i), voteList.get(i));
        }

        int actualDislikes = comment.getDislike();

        // Validate
        assertEquals(expectedDislikes, actualDislikes);
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, like like like, 1",
            "Alice, dislike dislike dislike, 0",
            "Alice, like dislike, 0",
            "Alice, dislike like, 1",
    })
    @DisplayName("Test addUserVote method for Multiple Valid Votes from Single Same User for like count")
    public void testAddUserVotesSameUserLike(String name, String votes, int expectedLikes) {
        // Convert votes to lists
        String[] voteList = votes.split("\\s+");

        // Execute
        for (String s : voteList) {
            comment.addUserVote(name, s);
        }

        int actualLikes = comment.getLike();

        // Validate
        assertEquals(expectedLikes, actualLikes);
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, like like like, 0",
            "Alice, dislike dislike dislike, 1",
            "Alice, like dislike, 1",
            "Alice, dislike like, 0",
    })
    @DisplayName("Test addUserVote method for Multiple Valid Votes from Single Same User for dislike count")
    public void testAddUserVotesSameUserDislike(String name, String votes, int expectedDislikes) {
        // Convert votes to lists
        String[] voteList = votes.split("\\s+");

        // Execute
        for (String s : voteList) {
            comment.addUserVote(name, s);
        }

        int actualDislikes = comment.getDislike();

        // Validate
        assertEquals(expectedDislikes, actualDislikes);
    }

    @ParameterizedTest
    @ValueSource( strings = {
            "Like",
            "disLike",
            "invalid",
            "Dislike",
            "LIKE",
            "DISLIKE",
            "neutral",
    })
    @DisplayName("Test addUserVote method for Single Invalid Vote from Single User")
    public void testAddUserInvalidVote(String vote) {
        // Execute & Validate
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> comment.addUserVote("Alice", vote));

        assertEquals("Invalid vote type", exception.getMessage());
    }
}
