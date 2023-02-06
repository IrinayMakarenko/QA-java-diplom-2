package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.OrderClient;
import praktikum.client.UserClient;
import praktikum.generator.UserGenerator;
import praktikum.pojo.OrderRequest;
import praktikum.pojo.UserRequest;

import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class GetOrderUserTest {
    private UserRequest userRequest;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    private String orderId;

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
        orderId = orderClient.createOrder(accessToken, orderRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .extract()
                .jsonPath().get("order._id");
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
    @Description("Get order of user with authorization")
    public void orderOfAuthorizedUserShouldBeGet() {

        orderClient.getOrderOfUser(accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true))
                .body("orders[0]._id", equalTo(orderId));
    }

    @Test
    @DisplayName("Check status code and body of /api/orders endpoint")
    @Description("Get order of user without authorization")
    public void orderOfNotAuthorizedUserShouldNotBeGet() {

        orderClient.getOrderOfUser("")
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
