package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.generator.UserGenerator;
import praktikum.pojo.LoginRequest;
import praktikum.pojo.UserRequest;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class LoginUserTest {

    private UserRequest userRequest;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {

        userRequest = UserGenerator.getRandomUserRequest();
        userClient = new UserClient();

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
    @DisplayName("Check status code and body of /api/auth/login endpoint")
    @Description("Login of an existing user")
    public void userShouldBeAuthorized() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userRequest.getEmail());
        loginRequest.setPassword(userRequest.getPassword());
        userClient.loginUser(loginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/login endpoint")
    @Description("Login user with wrong email")
    public void userShouldNotBeAuthorizedWithWrongEmail() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("");
        loginRequest.setPassword(userRequest.getPassword());
        userClient.loginUser(loginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
    @Test
    @DisplayName("Check status code and body of /api/auth/login endpoint")
    @Description("Login user with wrong password")
    public void userShouldNotBeAuthorizedWithWrongPassword() {

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userRequest.getEmail());
        loginRequest.setPassword("");
        userClient.loginUser(loginRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"));
    }
}
