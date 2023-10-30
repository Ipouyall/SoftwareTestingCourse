package controller;

import controllers.CommoditiesController;
import exceptions.NotExistentCommodity;
import model.Commodity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommoditiesControllerTest {
    @Mock
    Baloot baloot;

    @Captor
    ArgumentCaptor<Commodity> commodityArgumentCaptor;

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
        when(baloot.getCommodityById(commodityFake.getId())).thenReturn(commodityFake);
        when(baloot.suggestSimilarCommodities(commodityFake)).thenReturn(new ArrayList<>());
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityFake.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }



}
