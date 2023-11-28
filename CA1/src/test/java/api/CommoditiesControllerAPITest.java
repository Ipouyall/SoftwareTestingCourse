package api;

import application.BalootApplication;
import controllers.CommoditiesController;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.ArrayList;

import static defines.Errors.NOT_EXISTENT_COMMODITY;
import static defines.Errors.NOT_EXISTENT_USER;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class CommoditiesControllerAPITest {
    private CommoditiesController commoditiesController;
    private MockMvc mockMvc;
    @Mock
    private Baloot baloot;

    @Autowired
    public CommoditiesControllerAPITest(CommoditiesController commoditiesController, MockMvc mockMvc) {
        this.commoditiesController = commoditiesController;
        this.mockMvc = mockMvc;
    }

    @BeforeEach
    public void init() {
        commoditiesController.setBaloot(baloot);
    }

    public static Commodity createFakeCommodity() {
        Commodity commodity = new Commodity();
        commodity.setId(String.valueOf(1));
        commodity.setName("product " + 1);
        return commodity;
    }

    public static Commodity createFakeCommodityWithId(String id) {
        Commodity commodity = new Commodity();
        commodity.setId(id);
        commodity.setName("product " + id);
        return commodity;
    }

    public static Comment createFakeComment() {
        return new Comment(1, "test@gmail.com", "test", 1, "this is comment");
    }

    @Test
    public void getCommoditiesTest() throws Exception {
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        when(baloot.getCommodities()).thenReturn((commoditiesFake));

        mockMvc.perform(get("/commodities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(commoditiesFake.size())))
                .andExpect(jsonPath("$[0].id", is(commoditiesFake.get(0).getId())))
                .andExpect(jsonPath("$[0].name", is(commoditiesFake.get(0).getName())));
    }

    @Test
    public void getCommodityExistTest() throws Exception {
        Commodity commodity = createFakeCommodity();

        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);

        mockMvc.perform(get("/commodities/" + commodity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commodity.getId())))
                .andExpect(jsonPath("$.name", is(commodity.getName())));
    }

    @Test
    public void getCommodityNotExistTest() throws Exception {
        String commodityId = "123";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        mockMvc.perform(get("/commodities/" + commodityId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    public void getSuggestedCommoditiesExistTest() throws Exception {
        Commodity commodityFake = createFakeCommodityWithId("1");

        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodityWithId("2"));
        commoditiesFake.add(createFakeCommodityWithId("3"));

        when(baloot.getCommodityById(commodityFake.getId())).thenReturn(commodityFake);
        when(baloot.suggestSimilarCommodities(commodityFake)).thenReturn(commoditiesFake);

        mockMvc.perform(get("/commodities/" + commodityFake.getId() + "/suggested")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(commoditiesFake.size())))
                .andExpect(jsonPath("$[0].id", is(commoditiesFake.get(0).getId())))
                .andExpect(jsonPath("$[0].name", is(commoditiesFake.get(0).getName())))
                .andExpect(jsonPath("$[1].id", is(commoditiesFake.get(1).getId())))
                .andExpect(jsonPath("$[1].name", is(commoditiesFake.get(1).getName())));
    }

    @Test
    public void getSuggestedCommoditiesNotExistTest() throws Exception {
        String commodityId = "123";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        mockMvc.perform(get("/commodities/" + commodityId + "/suggested")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void rateCommodityValidExist() throws Exception {
        String commodityId = "123";
        Commodity commodityFake = createFakeCommodityWithId(commodityId);
        String body = """
                {
                     "rate": "5",
                     "username": "ali"
                }
                 """;

        when(baloot.getCommodityById(commodityId)).thenReturn(commodityFake);

        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("rate added successfully!"));
    }

    @Test
    public void rateCommodityInValidRate() throws Exception {
        String commodityId = "123";
        Commodity commodityFake = createFakeCommodityWithId(commodityId);

        String body = """
                {
                     "rate": "15",
                     "username": "ali"
                }
                 """;
        when(baloot.getCommodityById(commodityId)).thenReturn(commodityFake);
        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("rate added successfully!"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"5.6", "not number", " 5"})
    public void rateCommodityInValid(String rate) throws Exception {
        String commodityId = "123";
        String body = "{\"rate\": \"" + rate + "\", \"username\": \"ali\"}";

        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(not(containsString("rate added successfully!"))));
    }

    @Test
    public void rateCommodityValidNotExist() throws Exception {
        String commodityId = "123";
        String body = """
                {
                     "rate": "5",
                     "username": "ali"
                }
                 """;

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        mockMvc.perform(post("/commodities/" + commodityId + "/rate")
                        .content(body)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_COMMODITY));
    }

    @Test
    public void addCommodityCommentValidExist() throws Exception {
        String commentText = "this is comment";
        String username = "ali";
        String commodityId = "123";
        String userEmail = username + "@test.ir";
        User userFake = new User(username, "", userEmail, "", "");

        String body = "{\"username\": \"" + username + "\", \"comment\": \"" + commentText + "\"}";

        when(baloot.getUserById(username)).thenReturn(userFake);

        mockMvc.perform(post("/commodities/" + commodityId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("comment added successfully!"));
    }

    @Test
    public void addCommodityCommentInValid() throws Exception {
        String commentText = "this is comment";
        String username = "ali";
        String commodityId = "adasdsa";
        String userEmail = username + "@test.ir";
        User userFake = new User(username, "", userEmail, "", "");
        String body = "{\"username\": \"" + username + "\", \"comment\": \"" + commentText + "\"}";

        when(baloot.getUserById(username)).thenReturn(userFake);

        mockMvc.perform(post("/commodities/" + commodityId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void addCommodityCommentValidNotExist() throws Exception {
        String commentText = "this is comment";
        String username = "not exist";
        String commodityId = "123";
        String body = "{\"username\": \"" + username + "\", \"comment\": \"" + commentText + "\"}";


        when(baloot.getUserById(username)).thenThrow(new NotExistentUser());

        mockMvc.perform(post("/commodities/" + commodityId + "/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));
    }

    @Test
    public void getCommodityCommentValidTest() throws Exception {
        int commodityId = 123;
        ArrayList<Comment> commentsFake = new ArrayList<>();
        commentsFake.add(createFakeComment());

        when(baloot.getCommentsForCommodity(commodityId)).thenReturn(commentsFake);

        mockMvc.perform(get("/commodities/" + commodityId + "/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(commentsFake.size())))
                .andExpect(jsonPath("$[0].id", is(commentsFake.get(0).getId())))
                .andExpect(jsonPath("$[0].userEmail", is(commentsFake.get(0).getUserEmail())))
                .andExpect(jsonPath("$[0].username", is(commentsFake.get(0).getUsername())))
                .andExpect(jsonPath("$[0].commodityId", is(commentsFake.get(0).getCommodityId())))
                .andExpect(jsonPath("$[0].text", is(commentsFake.get(0).getText())));
    }

    @Test
    public void getCommodityCommentInValidTest() throws Exception {
        String commodityId = "notnumber";

        mockMvc.perform(get("/commodities/" + commodityId + "/comment"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCommodityCommentNoCommentTest() throws Exception {
        int commodityId = 123;

        when(baloot.getCommentsForCommodity(1)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/commodities/" + commodityId + "/comment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    public void searchCommoditiesByNameTest() throws Exception {
        String searchOption = "name";
        String searchValue = "ali";
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        String body = "{\"searchOption\": \"" + searchOption + "\", \"searchValue\": \"" + searchValue + "\"}";

        when(baloot.filterCommoditiesByName(searchValue)).thenReturn(commoditiesFake);

        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(commoditiesFake.size())))
                .andExpect(jsonPath("$[0].id", is(commoditiesFake.get(0).getId())))
                .andExpect(jsonPath("$[0].name", is(commoditiesFake.get(0).getName())));
    }

    @Test
    public void searchCommoditiesByCategoryTest() throws Exception {
        String searchOption = "category";
        String searchValue = "ali";

        String body = "{\"searchOption\": \"" + searchOption + "\", \"searchValue\": \"" + searchValue + "\"}";

        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        when(baloot.filterCommoditiesByCategory(searchValue)).thenReturn(commoditiesFake);

        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(commoditiesFake.size())))
                .andExpect(jsonPath("$[0].id", is(commoditiesFake.get(0).getId())))
                .andExpect(jsonPath("$[0].name", is(commoditiesFake.get(0).getName())));
    }

    @Test
    public void searchCommoditiesByProviderTest() throws Exception {
        String searchOption = "provider";
        String searchValue = "ali";
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        String body = "{\"searchOption\": \"" + searchOption + "\", \"searchValue\": \"" + searchValue + "\"}";


        when(baloot.filterCommoditiesByProviderName(searchValue)).thenReturn(commoditiesFake);


        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(commoditiesFake.size())))
                .andExpect(jsonPath("$[0].id", is(commoditiesFake.get(0).getId())))
                .andExpect(jsonPath("$[0].name", is(commoditiesFake.get(0).getName())));
    }

    @Test
    public void searchCommoditiesByInvalidOptionTest() throws Exception {
        String searchOption = "not option";
        String searchValue = "ali";

        String body = "{\"searchOption\": \"" + searchOption + "\", \"searchValue\": \"" + searchValue + "\"}";

        mockMvc.perform(post("/commodities/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }

}
