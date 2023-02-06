package praktikum.client;

import io.restassured.response.ValidatableResponse;
import praktikum.pojo.OrderRequest;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestClient {
    private static final String ORDER_CREATE = "/api/orders";
    private static final String INGREDIENTS_GET = "/api/ingredients";
    private static final String ORDERS_USER_GET = "/api/orders";

    public ValidatableResponse createOrder(String accessToken, OrderRequest orderRequest) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .body(orderRequest)
                .post(ORDER_CREATE)
                .then();
    }

    public ValidatableResponse getIngredients() {
        return given()
                .spec(getDefaultRequestSpec())
                .get(INGREDIENTS_GET)
                .then();
    }

    public ValidatableResponse getOrderOfUser(String accessToken) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .get(ORDERS_USER_GET)
                .then();
    }
}
