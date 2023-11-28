package api;


import application.BalootApplication;
import controllers.UserController;
import exceptions.NotExistentUser;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import static defines.Errors.INVALID_CREDIT_RANGE;
import static defines.Errors.NOT_EXISTENT_USER;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class UserControllerAPITest {

    private UserController userController;
    private MockMvc mockMvc;
    @Mock
    private Baloot baloot;

    @Autowired
    public UserControllerAPITest(UserController userController, MockMvc mockMvc) {
        this.userController = userController;
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void init() {
        userController.setBaloot(baloot);
    }

    private User createAnonymousUser() {
        return new User(
                "ali",
                "1234",
                "ali.n.hodaei@gmail.com",
                "2002-01-02",
                "Iran, Tehran"
        );
    }

    @Test
    public void getUserTestWithExistUserTest() throws Exception {
        String userId = "1";
        User user = createAnonymousUser();
        when(baloot.getUserById(userId)).thenReturn(user);
        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.password", is(user.getPassword())))
                .andExpect(jsonPath("$.email", is(user.getEmail())))
                .andExpect(jsonPath("$.birthDate", is(user.getBirthDate())))
                .andExpect(jsonPath("$.address", is(user.getAddress())));
    }

    @Test
    public void getUserWithNotExistUser() throws Exception {
        String userId = "1";
        when(baloot.getUserById(userId)).thenThrow(new NotExistentUser());
        mockMvc.perform(get("/users/{id}", userId)).andExpect(status().isNotFound());
    }

    @Test
    public void addCreditValidCreditExistUserTest() throws Exception {
        String userId = "1";
        User user = createAnonymousUser();
        when(baloot.getUserById(userId)).thenReturn(user);
        String body = """
                {
                     "credit": "100.0"
                }
                 """;
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("credit added successfully!"));
    }

    @Test
    public void addCreditInValidCreditTest() throws Exception {
        User user = createAnonymousUser();
        String userId = "1";
        String body = """
                {
                     "credit": "-100.0"
                }
                 """;
        when(baloot.getUserById(userId)).thenReturn(user);
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_CREDIT_RANGE));
    }

    @Test
    public void addCreditValidCreditNotExistUserTest() throws Exception {
        String userId = "1";
        when(baloot.getUserById(userId)).thenThrow(new NotExistentUser());
        String body = """
                {
                     "credit": "100.0"
                }
                 """;
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));
    }

    @Test
    public void addCreditInValidNumberFormatTest() throws Exception {
        String userId = "1";
        String body = """
                {
                     "credit": "Hello"
                }
                 """;
        mockMvc.perform(post("/users/" + userId + "/credit")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Please enter a valid number for the credit amount."));
    }
}