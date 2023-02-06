package praktikum;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.client.UserClient;
import praktikum.generator.UserGenerator;
import praktikum.pojo.ChangeUserRequest;
import praktikum.pojo.LoginRequest;
import praktikum.pojo.UserRequest;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class ChangeUserTest {
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

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userRequest.getEmail());
        loginRequest.setPassword(userRequest.getPassword());
        userClient.loginUser(loginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
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
    @DisplayName("Check status code and body of /api/auth/user endpoint")
    @Description("Change email with authorization")
    public void authorizedUserShouldBeChangedEmail() {

        ChangeUserRequest changeUserRequest = new ChangeUserRequest();
        changeUserRequest.setEmail("123dkjfsldkjf@yandex.ru");
        userClient.changeUser(accessToken, changeUserRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(changeUserRequest.getEmail());
        loginRequest.setPassword(userRequest.getPassword());
        userClient.loginUser(loginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/user endpoint")
    @Description("Change password with authorization")
    public void authorizedUserShouldBeChangedPassword() {

        ChangeUserRequest changeUserRequest = new ChangeUserRequest();
        changeUserRequest.setPassword("987654321");
        userClient.changeUser(accessToken, changeUserRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(userRequest.getEmail());
        loginRequest.setPassword(changeUserRequest.getPassword());
        userClient.loginUser(loginRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/user endpoint")
    @Description("Change name with authorization")
    public void authorizedUserShouldBeChangedName() {

        ChangeUserRequest changeUserRequest = new ChangeUserRequest();
        changeUserRequest.setName("BabaYaga");
        userClient.changeUser(accessToken, changeUserRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", equalTo(true));

        userClient.getUser(accessToken)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.name", equalTo("BabaYaga"));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/user endpoint")
    @Description("Change field without authorization")
    public void NotAuthorizedUserShouldNotBeChanged() {

        ChangeUserRequest changeUserRequest = new ChangeUserRequest();
        changeUserRequest.setEmail("123dkjfsldkjf@yandex.ru");
        userClient.changeUser("", changeUserRequest)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}
