package model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommentTest {

    private Comment comment;
    private int id;
    private String email;
    private String name;
    private int cId;
    private String content;

    @BeforeEach
    public void setUp() {
        id = 1;
        email = "user@example.com";
        name = "John";
        cId = 123;
        content = "Sample comment";

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
            "like, 1, 0",
            "dislike, 0, 1",
    })
    @DisplayName("Test addUserVote method")
    public void testAddUserVote(String vote, int expectedLikes, int expectedDislikes) {
        // Execute
        comment.addUserVote("Alice", vote);
        int actualLikes = comment.getLike();
        int actualDislikes = comment.getDislike();

        // Validate
        assertEquals(expectedLikes, actualLikes);
        assertEquals(expectedDislikes, actualDislikes);
    }

    @ParameterizedTest
    @CsvSource({
            "Ali Adam Alice, like like like, 3, 0",
            "Ali Adam Alice, dislike dislike dislike, 0, 3",
            "Ali Adam Alice, like dislike like, 2, 1",
            "Ali Adam Alice, dislike like dislike, 1, 2",
    })
    @DisplayName("Test addUserVote method for many votes from different users")
    public void testAddUserVotesDifferentUser(String names, String votes, int expectedLikes, int expectedDislikes) {
        // Convert names and votes to lists
        List<String> nameList = Arrays.asList(names.split("\\s+"));
        List<String> voteList = Arrays.asList(votes.split("\\s+"));

        // Execute
        for (int i = 0; i < nameList.size(); i++) {
            comment.addUserVote(nameList.get(i), voteList.get(i));
        }

        int actualLikes = comment.getLike();
        int actualDislikes = comment.getDislike();

        // Validate
        assertEquals(expectedLikes, actualLikes);
        assertEquals(expectedDislikes, actualDislikes);
    }

    @ParameterizedTest
    @CsvSource({
            "Alice, like like like, 1, 0",
            "Alice, dislike dislike dislike, 0, 1",
            "Alice, like dislike, 0, 1",
            "Alice, dislike like, 1, 0",
    })
    @DisplayName("Test addUserVote method for many votes from same user")
    public void testAddUserVotesSameUser(String name, String votes, int expectedLikes, int expectedDislikes) {
        // Convert votes to lists
        String[] voteList = votes.split("\\s+");

        // Execute
        for (String s : voteList) {
            comment.addUserVote(name, s);
        }

        int actualLikes = comment.getLike();
        int actualDislikes = comment.getDislike();

        // Validate
        assertEquals(expectedLikes, actualLikes);
        assertEquals(expectedDislikes, actualDislikes);
    }

    // TODO: does we need to check what happen if we use sth except like/dislike?
}
