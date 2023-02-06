package praktikum.client;

import io.restassured.response.ValidatableResponse;
import praktikum.pojo.ChangeUserRequest;
import praktikum.pojo.LoginRequest;
import praktikum.pojo.UserRequest;

import static io.restassured.RestAssured.given;

public class UserClient extends RestClient{
    private static final String USER_DELETE = "/api/auth/user";
    private static final String USER = "/api/auth/register";
    private static final String USER_LOGIN = "/api/auth/login";
    private static final String USER_CHANGE = "/api/auth/user";
    private static final String USER_GET = "/api/auth/user";


    public ValidatableResponse createUser(UserRequest userRequest) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(userRequest)
                .post(USER)
                .then();
    }

    public ValidatableResponse loginUser(LoginRequest loginRequest) {
        return given()
                .spec(getDefaultRequestSpec())
                .body(loginRequest)
                .post(USER_LOGIN)
                .then();
    }

    public ValidatableResponse deleteUser(String accessToken) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .delete(USER_DELETE)
                .then();
    }

    public ValidatableResponse changeUser(String accessToken, ChangeUserRequest changeUserRequest) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .body(changeUserRequest)
                .patch(USER_CHANGE)
                .then();
    }

    public ValidatableResponse getUser(String accessToken) {
        return given()
                .spec(getDefaultRequestSpec())
                .header("Authorization", accessToken)
                .get(USER_GET)
                .then();
    }
}
