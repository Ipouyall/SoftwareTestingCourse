package controller;

import controllers.CommentController;
import service.Baloot;
import model.Comment;
import exceptions.NotExistentComment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static defines.Errors.NOT_EXISTENT_COMMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {

    @Mock
    private Baloot baloot;

    @Mock
    private Comment comment;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    public void setUp() {
        commentController = new CommentController();
        commentController.setBaloot(baloot);
    }

    @Test
    public void testLikeCommentSuccess() throws Exception {
        // Initiate values
        int commentId = 1;
        String username = "user1";

        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        // Define mock behave
        when(baloot.getCommentById(commentId)).thenReturn(comment);

        // Exercise
        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        // Validate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully liked!", response.getBody());
        verify(baloot, times(1)).getCommentById(commentId);
        verify(comment, times(1)).addUserVote(username, "like");

        // Tear-down
    }

    @Test
    public void testLikeCommentFailNotExistent() throws Exception {
        // Initiate values
        int commentId = 2;
        String username = "user2";

        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        // Define mock behave
        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());

        // Exercise
        ResponseEntity<String> response = commentController.likeComment(String.valueOf(commentId), input);

        // Validate
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMENT, response.getBody());
        verify(baloot, times(1)).getCommentById(commentId);
        verify(comment, times(0)).addUserVote(any(), any());

        // Tear-down
    }

    @Test
    public void testDislikeCommentSuccess() throws Exception {
        // Initiate values
        int commentId = 1;
        String username = "user1";

        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        // Define mock behave
        when(baloot.getCommentById(commentId)).thenReturn(comment);

        // Exercise
        ResponseEntity<String> response = commentController.dislikeComment(String.valueOf(commentId), input);

        // Validate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully disliked!", response.getBody());
        verify(baloot, times(1)).getCommentById(commentId);
        verify(comment, times(1)).addUserVote(username, "dislike");

        // Tear-down
    }

    @Test
    public void testDislikeFailCommentNotExistent() throws Exception {
        // Initiate values
        int commentId = 2;
        String username = "user2";

        Map<String, String> input = new HashMap<>();
        input.put("username", username);

        // Define mock behave
        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());

        // Exercise
        ResponseEntity<String> response = commentController.dislikeComment(String.valueOf(commentId), input);

        // Validate
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMENT, response.getBody());
        verify(baloot, times(1)).getCommentById(commentId);
        verify(comment, times(0)).addUserVote(any(), any());

        // Tear-down
    }
}
