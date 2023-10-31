package controller;

import controllers.AuthenticationController;
import exceptions.InvalidRequestFormat;
import exceptions.UsernameAlreadyTaken;
import model.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.junit.jupiter.MockitoExtension;
import service.Baloot;
import model.User;
import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static defines.Errors.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private Baloot baloot;

    @InjectMocks
    private AuthenticationController authenticationController;

    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    public static User createAnonymousUser(){
        String username = "john";
        String password = "due";
        String email = "fake@fakemail.com";
        String birthDate = "1990-01-01";
        String address = "Tehran, Iran";

        return new User(username, password, email, birthDate, address);
    }

    @BeforeEach
    public void setUp() {
        authenticationController = new AuthenticationController();
        authenticationController.setBaloot(baloot);
    }

    @Test
    public void testLoginSuccess() throws Exception {
        // Initiate values
        User user = createAnonymousUser();
        String username = user.getUsername();
        String password = user.getPassword();

        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        // Exercise
        ResponseEntity<String> response = authenticationController.login(input);

        // Validate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("login successfully!", response.getBody());
        verify(baloot).login(username, password);

        // Tear-down
    }

    @Test
    public void testLoginFailNotExistentUser() throws NotExistentUser, IncorrectPassword {
        User user = createAnonymousUser();
        String username = user.getUsername();
        String password = user.getPassword();

        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        // Define mock behave
        doThrow(new NotExistentUser()).when(baloot).login(username, password);

        // Exercise
        ResponseEntity<String> response = authenticationController.login(input);

        // Validate
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_USER, response.getBody());
        verify(baloot, times(1)).login(username, password);

        // Tear-down
    }

    @Test
    public void testLoginFailIncorrectPassword() throws Exception {
        // Initiate values
        User user = createAnonymousUser();
        String username = user.getUsername();
        String password = user.getPassword();

        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);

        // Define mock behave
        doThrow(new IncorrectPassword()).when(baloot).login(username, password);

        // Exercise
        ResponseEntity<String> response = authenticationController.login(input);

        // Validate
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(INCORRECT_PASSWORD, response.getBody());
        verify(baloot, times(1)).login(username, password);

        // Tear-down
    }

    @Test
    public void testSignupSuccess() throws Exception {
        // Initiate values
        User user = createAnonymousUser();
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String birthDate = user.getBirthDate();
        String address = user.getAddress();

        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);
        input.put("email", email);
        input.put("birthDate", birthDate);
        input.put("address", address);

        // Exercise
        ResponseEntity<String> response = authenticationController.signup(input);

        // Validate
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("signup successfully!", response.getBody());
        verify(baloot, times(1)).addUser(any(User.class));

        verify(baloot).addUser(userArgumentCaptor.capture());
        User genUser = userArgumentCaptor.getValue();
        assertEquals(user.getUsername(), genUser.getUsername());
        assertEquals(user.getPassword(), genUser.getPassword());
        assertEquals(user.getEmail(), genUser.getEmail());
        assertEquals(user.getBirthDate(), genUser.getBirthDate());
        assertEquals(user.getAddress(), genUser.getAddress());

        // Tear-down
    }

    @Test
    public void testSignupFailUsernameAlreadyTaken() throws Exception {
        // Initiate values
        User user = createAnonymousUser();
        String username = user.getUsername();
        String password = user.getPassword();
        String email = user.getEmail();
        String birthDate = user.getBirthDate();
        String address = user.getAddress();

        Map<String, String> input = new HashMap<>();
        input.put("username", username);
        input.put("password", password);
        input.put("email", email);
        input.put("birthDate", birthDate);
        input.put("address", address);

        doThrow(new UsernameAlreadyTaken()).when(baloot).addUser(any(User.class));

        // Exercise
        ResponseEntity<String> response = authenticationController.signup(input);

        // Validate
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(USERNAME_ALREADY_TAKEN, response.getBody());
        verify(baloot, times(1)).addUser(any(User.class));

        verify(baloot).addUser(userArgumentCaptor.capture());
        User genUser = userArgumentCaptor.getValue();
        assertEquals(user.getUsername(), genUser.getUsername());
        assertEquals(user.getPassword(), genUser.getPassword());
        assertEquals(user.getEmail(), genUser.getEmail());
        assertEquals(user.getBirthDate(), genUser.getBirthDate());
        assertEquals(user.getAddress(), genUser.getAddress());

        // Tear-down
    }

    @Nested
    class FailureDueToMissingData {

        @ParameterizedTest
        @ValueSource(strings = {"password", "username"})
        public void testLoginFailSomethingNotProvided(String removed) throws Exception {
            // Initiate values
            User user = createAnonymousUser();
            String username = user.getUsername();
            String password = user.getPassword();

            Map<String, String> input = new HashMap<>();
            if (!removed.equals("username"))
                input.put("username", username);
            if (!removed.equals("password"))
                input.put("password", password);

            // Exercise
            ResponseEntity<String> response = authenticationController.login(input);

            // Validate
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(INVALID_REQUEST_FORMAT, response.getBody());
            verify(baloot, times(0)).login(any(), any());

            // Tear-down
        }

        @ParameterizedTest
        @ValueSource(strings = {"password", "username", "email", "birthDate", "address"})
        public void testSignupFailSomethingNotProvided(String removed) throws Exception {
            // Initiate values
            User user = createAnonymousUser();
            String username = user.getUsername();
            String password = user.getPassword();
            String email = user.getEmail();
            String birthDate = user.getBirthDate();
            String address = user.getAddress();

            Map<String, String> input = new HashMap<>();
            if (!removed.equals("username"))
                input.put("username", username);
            if (!removed.equals("password"))
                input.put("password", password);
            if (!removed.equals("email"))
                input.put("email", email);
            if (!removed.equals("birthDate"))
                input.put("birthDate", birthDate);
            if (!removed.equals("address"))
                input.put("address", address);

            // Exercise
            ResponseEntity<String> response = authenticationController.signup(input);

            // Validate
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
            assertEquals(INVALID_REQUEST_FORMAT, response.getBody());
            verify(baloot, times(0)).addUser(any());

            // Tear-down
        }
    }
}
