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

public class CreationUserTest {
    private UserRequest userRequest;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {

        userRequest = UserGenerator.getRandomUserRequest();
        userClient = new UserClient();
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
    @DisplayName("Check status code and body of /api/auth/register endpoint")
    @Description("Create unique user")
    public void uniqueUserShouldBeCreated() {

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

    @Test
    @DisplayName("Check status code and body of /api/auth/register endpoint")
    @Description("Create user who is already registered")
    public void userAlreadyRegisteredShouldNotBeCreated() {

        userRequest.setEmail("testIrina184503477-data@yandex.ru");
        userRequest.setPassword("1234567");
        userRequest.setName("Irina");
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

        userClient.createUser(userRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register endpoint")
    @Description("Create user without email")
    public void userWithoutEmailShouldNotBeCreated() {

        userRequest.setEmail("");
        userClient.createUser(userRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register endpoint")
    @Description("Create user without password")
    public void userWithoutPasswordShouldNotBeCreated() {

        userRequest.setPassword("");
        userClient.createUser(userRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Check status code and body of /api/auth/register endpoint")
    @Description("Create user without name")
    public void userWithoutNameShouldNotBeCreated() {

        userRequest.setName("");
        userClient.createUser(userRequest)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
