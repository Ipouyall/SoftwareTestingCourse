package controller;

import controllers.CommoditiesController;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static defines.Errors.NOT_EXISTENT_COMMODITY;
import static defines.Errors.NOT_EXISTENT_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommoditiesControllerTest {
    @Mock
    Baloot baloot;

    @Captor
    ArgumentCaptor<Comment> commentArgumentCaptor;

    CommoditiesController commoditiesController;

    @BeforeEach
    public void init() {
        commoditiesController = new CommoditiesController();
        commoditiesController.setBaloot(baloot);
    }

    public static Commodity createFakeCommodity() {
        Commodity commodity = new Commodity();
        commodity.setId(String.valueOf(1));
        commodity.setName("product " + 1);
        return commodity;
    }

    public static Comment createFakeComment() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setText("this is test comment");
        return comment;
    }

    @Test
    public void getCommoditiesTest() {
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        when(baloot.getCommodities()).thenReturn(commoditiesFake);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();

        verify(baloot, times(1)).getCommodities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), commoditiesFake.size());
        assertEquals(response.getBody().get(0).getId(), commoditiesFake.get(0).getId());
        assertEquals(response.getBody().get(0).getName(), commoditiesFake.get(0).getName());
    }

    @Test
    public void getCommodityExistTest() throws NotExistentCommodity {
        Commodity commodity = createFakeCommodity();

        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodity.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commodity.getId(), response.getBody().getId());
        assertEquals(commodity.getName(), response.getBody().getName());
    }

    @Test
    public void getCommodityNotExistTest() throws NotExistentCommodity {
        String commodityId = "123";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void getSuggestedCommoditiesExistTest() throws NotExistentCommodity {
        Commodity commodityFake = createFakeCommodity();

        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());
        commoditiesFake.add(createFakeCommodity());

        when(baloot.getCommodityById(commodityFake.getId())).thenReturn(commodityFake);
        when(baloot.suggestSimilarCommodities(commodityFake)).thenReturn(commoditiesFake);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityFake.getId());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), commoditiesFake.size());
        assertEquals(response.getBody().get(0).getId(), commoditiesFake.get(0).getId());
        assertEquals(response.getBody().get(0).getName(), commoditiesFake.get(0).getName());
    }


    @Test
    public void getSuggestedCommoditiesNotExistTest() throws NotExistentCommodity {
        String commodityId = "123";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void rateCommodityValidExist() throws NotExistentCommodity {
        Map<String, String> inputFake = new HashMap<>();
        String rateString = "5";
        String username = "ali";
        inputFake.put("rate", rateString);
        inputFake.put("username", username);
        Commodity commodityFake = mock(Commodity.class);
        String commodityId = "123";

        when(baloot.getCommodityById(commodityId)).thenReturn(commodityFake);

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, inputFake);

        verify(commodityFake, times(1)).addRate(username, Integer.parseInt(rateString));
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("rate added successfully!", response.getBody());
    }

    @Test
    public void rateCommodityInValid() throws NotExistentCommodity {
        Map<String, String> inputFake = new HashMap<>();
        String rateString = "not number";
        String username = "ali";
        inputFake.put("rate", rateString);
        inputFake.put("username", username);
        String commodityId = "123";

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, inputFake);

        verify(baloot, times(0)).getCommodityById(commodityId);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void rateCommodityValidNotExist() throws NotExistentCommodity {
        Map<String, String> inputFake = new HashMap<>();
        String rateString = "5";
        String username = "ali";
        inputFake.put("rate", rateString);
        inputFake.put("username", username);
        String commodityId = "123";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, inputFake);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_COMMODITY, response.getBody());
    }


    @Test
    public void addCommodityCommentValidExist() throws NotExistentCommodity, NotExistentUser {
        Map<String, String> inputFake = new HashMap<>();
        String commentText = "this is comment";
        String username = "ali";
        String commodityId = "123";
        String userEmail = username + "@test.ir";
        inputFake.put("comment", commentText);
        inputFake.put("username", username);
        User userFake = new User(username, "", userEmail, "", "");

        when(baloot.getUserById(username)).thenReturn(userFake);

        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodityId, inputFake);

        verify(baloot).addComment(commentArgumentCaptor.capture());

        Comment comment = commentArgumentCaptor.getValue();

        assertEquals(userEmail, comment.getUserEmail());
        assertEquals(username, comment.getUsername());
        assertEquals(commentText, comment.getText());
        assertEquals(commodityId, String.valueOf(comment.getCommodityId()));


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("comment added successfully!", response.getBody());
    }

    @Test
    public void addCommodityCommentInValid() throws NotExistentUser {
        Map<String, String> inputFake = new HashMap<>();
        String commentText = "this is comment";
        String username = "ali";
        String commodityId = "adasdsa";
        inputFake.put("comment", commentText);
        inputFake.put("username", username);

        when(baloot.getUserById(username)).thenReturn(new User());

        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodityId, inputFake);

        verify(baloot, times(0)).addComment(any(Comment.class));
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void addCommodityCommentValidNotExist() throws NotExistentUser {
        Map<String, String> inputFake = new HashMap<>();
        String commentText = "this is comment";
        String username = "ali";
        String commodityId = "123";
        inputFake.put("comment", commentText);
        inputFake.put("username", username);

        when(baloot.getUserById(username)).thenThrow(new NotExistentUser());

        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodityId, inputFake);

        verify(baloot, times(0)).addComment(any(Comment.class));
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(NOT_EXISTENT_USER, response.getBody());
    }

    @Test
    public void getCommodityCommentValidTest() {
        int commodityId = 123;
        ArrayList<Comment> commentsFake = new ArrayList<>();
        commentsFake.add(createFakeComment());

        when(baloot.getCommentsForCommodity(commodityId)).thenReturn(commentsFake);
        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment(String.valueOf(commodityId));

        verify(baloot, times(1)).getCommentsForCommodity(commodityId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), commentsFake.size());
        assertEquals(response.getBody().get(0).getId(), commentsFake.get(0).getId());
        assertEquals(response.getBody().get(0).getText(), commentsFake.get(0).getText());
    }

    @Test
    public void getCommodityCommentInValidTest() {
        String commodityId = "not number";

        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment(commodityId);

        verify(baloot, times(0)).getCommentsForCommodity(anyInt());
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void searchCommoditiesByNameTest() {
        Map<String, String> inputFake = new HashMap<>();
        String searchOption = "name";
        String searchValue = "ali";
        inputFake.put("searchOption", searchOption);
        inputFake.put("searchValue", searchValue);
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        when(baloot.filterCommoditiesByName(searchValue)).thenReturn(commoditiesFake);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(inputFake);

        verify(baloot, times(1)).filterCommoditiesByName(searchValue);
        verify(baloot, times(0)).filterCommoditiesByCategory(anyString());
        verify(baloot, times(0)).filterCommoditiesByProviderName(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), commoditiesFake.size());
        assertEquals(response.getBody().get(0).getId(), commoditiesFake.get(0).getId());
        assertEquals(response.getBody().get(0).getName(), commoditiesFake.get(0).getName());
    }

    @Test
    public void searchCommoditiesByCategoryTest() {
        Map<String, String> inputFake = new HashMap<>();
        String searchOption = "category";
        String searchValue = "ali";
        inputFake.put("searchOption", searchOption);
        inputFake.put("searchValue", searchValue);
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        when(baloot.filterCommoditiesByCategory(searchValue)).thenReturn(commoditiesFake);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(inputFake);

        verify(baloot, times(1)).filterCommoditiesByCategory(searchValue);
        verify(baloot, times(0)).filterCommoditiesByName(anyString());
        verify(baloot, times(0)).filterCommoditiesByProviderName(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), commoditiesFake.size());
        assertEquals(response.getBody().get(0).getId(), commoditiesFake.get(0).getId());
        assertEquals(response.getBody().get(0).getName(), commoditiesFake.get(0).getName());
    }

    @Test
    public void searchCommoditiesByProviderTest() {
        Map<String, String> inputFake = new HashMap<>();
        String searchOption = "provider";
        String searchValue = "ali";
        inputFake.put("searchOption", searchOption);
        inputFake.put("searchValue", searchValue);
        ArrayList<Commodity> commoditiesFake = new ArrayList<>();
        commoditiesFake.add(createFakeCommodity());

        when(baloot.filterCommoditiesByProviderName(searchValue)).thenReturn(commoditiesFake);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(inputFake);

        verify(baloot, times(1)).filterCommoditiesByProviderName(searchValue);
        verify(baloot, times(0)).filterCommoditiesByName(anyString());
        verify(baloot, times(0)).filterCommoditiesByCategory(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), commoditiesFake.size());
        assertEquals(response.getBody().get(0).getId(), commoditiesFake.get(0).getId());
        assertEquals(response.getBody().get(0).getName(), commoditiesFake.get(0).getName());
    }

    @Test
    public void searchCommoditiesByInvalidOptionTest() {
        Map<String, String> inputFake = new HashMap<>();
        String searchOption = "not option";
        String searchValue = "ali";
        inputFake.put("searchOption", searchOption);
        inputFake.put("searchValue", searchValue);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(inputFake);

        verify(baloot, times(0)).filterCommoditiesByProviderName(searchValue);
        verify(baloot, times(0)).filterCommoditiesByName(anyString());
        verify(baloot, times(0)).filterCommoditiesByCategory(anyString());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody().size(), 0);
    }
}
