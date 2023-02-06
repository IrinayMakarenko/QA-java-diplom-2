package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.generator.IngredientsGenerator;
import praktikum.generator.UserGenerator;
import praktikum.pojo.OrderRequest;
import praktikum.pojo.UserRequest;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class CreationOrderTest {
    private UserRequest userRequest;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @Before
    public void setUp() {

        userRequest = UserGenerator.getRandomUserRequest();
        userClient = new UserClient();
        orderClient = new OrderClient();

        accessToken = userClient.createUser(userRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .path("accessToken");
    }

    @After
    public void tearDown() {

        if (accessToken != null) {
            userClient.deleteUser(accessToken)
                    .assertThat()
                    .statusCode(SC_ACCEPTED)
                    .and()
                    .body("success", equalTo(true));
        }
    }

    @Test
    @DisplayName("Check status code and body of /api/orders endpoint")
    @Description("Create order with authorization and with ingredients")
    public void orderShouldBeCreatedWithAuthorizationAndIngredients() {

        List<String> data = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .jsonPath().getList("data._id");

        OrderRequest orderRequest = new OrderRequest();
        List<String> ids = List.of(data.get(0), data.get(1));
        orderRequest.setIngredients(ids);
        orderClient.createOrder(accessToken, orderRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order._id", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Check status code and body of /api/orders endpoint")
    @Description("Create order without authorization and with ingredients")
    public void orderShouldBeCreatedWithoutAuthorizationAndWithIngredients() {

        List<String> data = orderClient.getIngredients()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .jsonPath().getList("data._id");

        OrderRequest orderRequest = new OrderRequest();
        List<String> ids = List.of(data.get(0), data.get(1));
        orderRequest.setIngredients(ids);
        orderClient.createOrder("", orderRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("order.number", Matchers.notNullValue());
    }

    @Test
    @DisplayName("Check status code and body of /api/orders endpoint")
    @Description("Create order without ingredients")
    public void orderShouldNotBeCreatedWithoutIngredients() {

        OrderRequest orderRequest = new OrderRequest();
        orderClient.createOrder(accessToken, orderRequest)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Check status code and body of /api/orders endpoint")
    @Description("Create order with wrong ingredients")
    public void orderShouldNotBeCreatedWithWrongHashOfIngredients() {

        OrderRequest orderRequest = new OrderRequest();
        List<String> ids = IngredientsGenerator.getRandomIngredients();
        orderRequest.setIngredients(ids);
        orderClient.createOrder(accessToken, orderRequest)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }
}
